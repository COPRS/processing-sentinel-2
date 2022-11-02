import tabulate


def get_global_status(statuslist):
    print(len(statuslist))
    if len(statuslist) == 0:
        return True
    for p in range(len(statuslist)):
        len(statuslist[p])
        if len(statuslist[p]) == 0:
            return True
        for t in range(len(statuslist[p])):
            if statuslist[p][t][7] == "ERROR":
                return True
    return False


def get_percentage(statuslist):
    percentage = 0.0
    total_pools = 0

    for p in range(len(statuslist)):
        current_pool_tasks = 0
        current_pool_finished_tasks = 0
        for t in range(len(statuslist[p])):
            if statuslist[p][t][7] == "FINISHED" or statuslist[p][t][7] != "SKIPPED":
                if statuslist[p][t][7] == "FINISHED":
                    current_pool_finished_tasks = current_pool_finished_tasks + 1
                current_pool_tasks = current_pool_tasks + 1

        current_pool_percentage = float(current_pool_finished_tasks) / float(current_pool_tasks)
        percentage = percentage + current_pool_percentage
        total_pools = total_pools + 1
    if total_pools != 0:
        return (100.0 * percentage) / total_pools
    else:
        return 0.0


def pretty_print(statuslist):
    headers = ["Pool", "Name", "Pid", "Task_id", "begin_time", "Time", "RAM", "Status ", "ExitCode"]
    statuses = []
    for p in range(len(statuslist)):
        for t in range(len(statuslist[p])):
            statuses.append(statuslist[p][t])
    return tabulate.tabulate(statuses, headers)


def sumary_print(statuslist):
    result_str = ""
    for p in range(len(statuslist)):
        total_tasks = 0
        finished_task = 0
        running_task = 0
        for t in range(len(statuslist[p])):
            if statuslist[p][t][7] == "FINISHED":
                finished_task = finished_task +1
            if statuslist[p][t][7] == "RUNNING":
                running_task = running_task + 1
            total_tasks = total_tasks + 1
        result_str = result_str + str(finished_task) + " / "+str(total_tasks)+ " : "
    return result_str


def output_results(status_list, mode, tasktable, dateoflogs, resulfile):
    with open(resulfile, "aw") as res:
        res.write("#########################\n")
        res.write("# Date : " + dateoflogs + "\n")
        res.write("# Mode : " + mode + "\n")
        res.write("# Tasktable : " + tasktable + "\n")
        global_status = get_global_status(status_list)
        if global_status:
            res.write("# Global status : ERROR\n")
        else:
            res.write("# Global status : SUCCESS\n")
        res.write(pretty_print(status_list))

        res.write("\n\n#########################\n\n\n")
