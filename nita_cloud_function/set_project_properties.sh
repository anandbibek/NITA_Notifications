#!/bin/sh

export project="nita-notifications"
export function_name="nita-notifications-cf"
export job_name="nita-notifications-cs"
export location="us-central1"
export schedule="0 7-23/2 * * *" # every day, every 2 hours from 07:00 to 23:00
export timezone="Asia/Kolkata"

export action="nita"