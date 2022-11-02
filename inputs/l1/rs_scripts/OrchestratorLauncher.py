# coding=utf-8
import datetime
import logging
import signal
import subprocess
import xmlrpclib
from time import sleep

from log_colorizer import make_colored_stream_handler

import Constants
from OrchestratorTools import get_global_status, pretty_print, get_percentage, output_results, sumary_print


class OrchestratorLauncher(object):

    def __init__(self, loglevel, port):
        self.servorch_process = None
        self.server_handle = None
        self.signal_received = False
        # Connect to servorch
        self.host = "localhost"
        self.port = port
        self.host_file = None
        self.logger = logging.getLogger("OrchestratorLauncher")
        self._handler = make_colored_stream_handler()
        self._handler.setFormatter(Constants.LOG_FORMATTER)
        self.logger.addHandler(self._handler)
        self.logger.setLevel(loglevel)

    def stop_orchestrator(self, sig, frame):
        self.logger.info("Stopping orchestrator service")

        try:
            connection_string_stop = 'http://' + self.host + ':' + str(self.port)
            server_handle_stop = xmlrpclib.ServerProxy(connection_string_stop, allow_none=True)
            alive = server_handle_stop.is_alive()
            server_handle_stop.stopall()
        except Exception as e:
            self.logger.error("Error while stopping orch")
            self.logger.error(e)
            pass
        # This one should be on error
        try:
            server_handle_stop.stopall()
            server_handle_stop.stop()
        except Exception as e:
            self.logger.debug(e)
            pass
        sleep(2)
        if self.servorch_process and self.servorch_process.poll() is None:

            self.logger.error("Orchestrator is not stopped !!!!")
            sleep(1)
            print(self.servorch_process.poll())
            if self.servorch_process.poll() is None:
                self.logger.error("Orchestrator is not stopped, Terminating process !!!!")
                self.servorch_process.terminate()
        self.logger.debug("Orchestrator stopped")
        if sig:
            exit(sig)

    def start_orchestrator(self, the_tasktable, mode, ipf_env, orchlogfile, orcherrfile, resultfile, dateoflogs,
                           skipped_pools, previous_context_file, poll_interval,
                           tile, kill_timeout):
        tile_str = ""
        if tile is not None:
            tile_str = "T" + tile

        # catch signals
        signal.signal(signal.SIGINT, self.stop_orchestrator)
        signal.signal(signal.SIGTERM, self.stop_orchestrator)
        signal.signal(signal.SIGHUP, self.stop_orchestrator)

        # launch Orchestrator service
        self.logger.info("Launching Orchestrators service in background")
        self.logger.info("Orch log file in : " + orchlogfile)
        logfile = open(orchlogfile, 'wb')
        errfile = open(orcherrfile, 'wb')
        self.servorch_process = subprocess.Popen(["servorch"], env=ipf_env, stdout=logfile, stderr=errfile, shell=True)
        # Sleep in order to wait for the service to initialize
        sleep(2)
        if self.servorch_process.poll() is not None:
            self.logger.warn("Retrying servorch due to previous failure ...")
            self.stop_orchestrator(None, None)
            self.servorch_process = subprocess.Popen(["servorch"], env=ipf_env, stdout=logfile, stderr=subprocess.STDOUT,
                                                shell=True)
            # wait till the service is able to accept connections
            sleep(2)
            if self.servorch_process.poll() is not None:
                self.logger.error("Return Code for servorch : " + str(self.servorch_process.returncode))
                self.logger.error("Check file :" + orchlogfile + " for more informations")
                logfile.close()
                errfile.close()
                raise Exception("Error while launching the orchestrator service, exit")

        # Connect to orchestrator xmlrpc service
        if "IDPORCH_PORT" in ipf_env:
            port = int(ipf_env["IDPORCH_PORT"])
        connection_string = 'http://' + self.host + ':' + str(self.port)
        self.server_handle = xmlrpclib.ServerProxy(connection_string, allow_none=True)
        # Test connection
        try:
            alive = self.server_handle.is_alive()
            self.logger.info("Orchestrator up and running")
        except Exception as e:
            self.logger.error("%s is not available." % connection_string)
            self.stop_orchestrator(None, None)
            logfile.close()
            errfile.close()
            raise Exception("Impossible to connect to servorch")

        # test if service can handle requests
        is_alive = self.server_handle.is_alive()
        if not is_alive:
            self.logger.error("Orchestrator is not able to handle requests , check logs in " + orchlogfile)
            self.stop_orchestrator(None, None)
            logfile.close()
            errfile.close()
            raise Exception("Orchestrator is not able to handle requests, check logs in " + orchlogfile)

        # Launch the tasktable task
        self.logger.info("Launch tasktable : " + the_tasktable)
        param_dict_orch = {"filename": the_tasktable}
        if skipped_pools != 0:
            param_dict_orch["skip"] = skipped_pools
        if previous_context_file is not None:
            param_dict_orch["context"] = previous_context_file
        # Start the request
        self.server_handle.start(param_dict_orch)

        # Wait till the request is finished
        self.logger.info("Waiting for task to finish")
        pipeline_finished = not self.server_handle.is_processing()
        timeout_reached = False
        timeout = kill_timeout # no timeout
        self.logger.info("Orchestrator timeout : "+str(timeout))
        start_time = datetime.datetime.now()
        while not pipeline_finished and not timeout_reached:
            pipeline_finished = not self.server_handle.is_processing()
            if (datetime.datetime.now() - start_time).seconds > timeout:
                timeout_reached = True
            status_list = self.server_handle.status(None)
            self.logger.info("Processing "+mode + " " + tile_str
                             + " : " + str(int(get_percentage(status_list))) + "% in " + str(
                (datetime.datetime.now() - start_time).seconds) + " seconds : "+sumary_print(status_list))
            sleep(poll_interval)
        # get the status
        status_list = self.server_handle.status(None)
        output_results(status_list, mode, the_tasktable, dateoflogs, resultfile)

        # timeout ?
        if timeout_reached:
            self.logger.error("Maximum time reached for processing " + str(timeout))
            self.stop_orchestrator(None, None)
            logfile.close()
            errfile.close()
            raise Exception("Maximum time reached for processing " + str(timeout))

        # Print global Status
        self.logger.info("#########################")
        self.logger.info("# Date : " + dateoflogs)
        self.logger.info("# Mode : " + mode)
        self.logger.info("# Tasktable : " + the_tasktable)
        self.logger.info("# Duration : "+str(
                (datetime.datetime.now() - start_time).seconds) + " seconds")
        global_status = get_global_status(status_list)
        if global_status:
            self.logger.info("# Global status : ERROR")
        else:
            self.logger.info("# Global status : SUCCESS")
        self.logger.info("\n" + pretty_print(status_list))
        self.logger.info("\n\n#########################")
        if get_global_status(status_list) == 1:
            self.stop_orchestrator(None, None)
            self.logger.error("Processing finished with error !!!")
            logfile.close()
            errfile.close()
            raise Exception("Processing finished with error !!!")
        else:
            self.logger.info("Finished processing successfully !!!")
        self.stop_orchestrator(None, None)
        logfile.close()
        errfile.close()
