#!/bin/bash

delete_scheduler_job_if_exists() {
    job_name="$1"
    location="$2"

    gcloud scheduler jobs describe "$job_name" \
        --project="$project" \
        --location "$location" >/dev/null 2>&1

    if [ $? -eq 0 ]; then
        gcloud scheduler jobs delete "$job_name" \
            --project="$project" \
            --location "$location" \
            --quiet
    else
        echo "[$job_name]: Does not exist."
    fi
}

create_scheduler_job() {
    job_name="$1"
    schedule="$2"
    timezone="$3"
    target_http_url="$4"
    location="$5"

    gcloud scheduler jobs describe "$job_name" >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "[$job_name]: Already exists. Skipping creation."
    else
        gcloud scheduler jobs create http "$job_name" \
            --project="$project" \
            --schedule "$schedule" \
            --time-zone "$timezone" \
            --http-method "POST" \
            --message-body "{\"action\": \"$action\"}" \
            --headers "Content-Type=application/json" \
            --uri "$target_http_url" \
            --location "$location" \

        if [ $? -eq 0 ]; then
          echo "[$job_name]: Created successfully."
        fi
    fi
}

. ./set_project_properties.sh
echo "Project: $project"
echo "[$job_name]: Reconfiguring..."

# Delete the job if it exists
delete_scheduler_job_if_exists "$job_name" "$location"

# target_http_url="https://us-central1-cloud-func-test-392314.cloudfunctions.net/web-notifications"
target_http_url="https://${location}-${project}.cloudfunctions.net/${function_name}"
echo "Target URL: $target_http_url"

# Create a new job if it doesn't exist
create_scheduler_job "$job_name" "$schedule" "$timezone" "$target_http_url" "$location"
