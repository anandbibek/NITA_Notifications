import json

from google.cloud import storage
from google.cloud.exceptions import NotFound
from google.cloud.storage.constants import PUBLIC_ACCESS_PREVENTION_ENFORCED

from util import is_running_on_cloud

# Constants
BUCKET_NAME = "com-anandbibek-nita-notifications-data"

# Create storage_client instance outside of functions
storage_client = storage.Client()


def write_data_to_file(filename, data):
    """Write data to a file in the bucket or locally."""
    if is_running_on_cloud():
        # Write to GCS
        bucket = get_bucket(BUCKET_NAME)
        blob = bucket.blob(filename)
        blob.upload_from_string(json.dumps(data))
    else:
        # Write locally
        with open(filename, "w") as file:
            json.dump(data, file)


def read_data_from_file(filename):
    """Read data from a file in the bucket or locally."""
    if is_running_on_cloud():
        # Read from GCS
        bucket = get_bucket(BUCKET_NAME)
        blob = bucket.blob(filename)
        try:
            return json.loads(blob.download_as_text())
        except NotFound:
            return ""
    else:
        # Read locally
        try:
            with open(filename, "r") as file:
                return json.load(file)
        except FileNotFoundError:
            return ""


def get_bucket(name):
    bucket = storage_client.bucket(name)
    if not bucket.exists():
        print("Creating bucket: ", name)
        bucket.create(location="us-central1")

        # Set the bucket's ACL to prevent public access
        bucket.iam_configuration.public_access_prevention = (
            PUBLIC_ACCESS_PREVENTION_ENFORCED
        )
        bucket.patch()

    return bucket
