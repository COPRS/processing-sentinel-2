# coding=utf-8


def do_l1a(datatake_type):
    return datatake_type in ["INS-RAW", "INS-DASC", "INS-ABSR", "INS-EOBS"]


def do_l1b(datatake_type):
    return datatake_type in ["INS-NOBS", "INS-VIC", "INS-RAW"]
