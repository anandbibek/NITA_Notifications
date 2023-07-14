import os


def get_diff_array(array1, array2):
    set1 = set(array1)
    set2 = set(array2)
    diff_set = set1 - set2
    diff_array = list(diff_set)
    print('DIFF: new {}, old {}, diff {}'.format(len(set1), len(set2), len(diff_array)))
    return diff_array


def map_data(array1):
    return {"data": array1}


def is_running_on_cloud():
    """Check if the code is running on a cloud platform (GCP)."""
    return "K_SERVICE" in os.environ
