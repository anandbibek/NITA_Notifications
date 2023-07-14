#!/bin/sh

. ./set_project_properties.sh
echo "Deploying... $project"

gcloud functions deploy "$function_name" \
  --gen2 \
  --project="$project" \
  --runtime="python311" \
  --region="$location" \
  --source=. \
  --entry-point="scan" \
  --trigger-http \
  --allow-unauthenticated \
  --ingress-settings internal-only \

