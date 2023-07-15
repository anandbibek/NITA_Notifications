from flask import jsonify

from messenger import send_notification
from scraper import get_page, get_links, get_texts, combine_arrays
from storage import read_data_from_file, write_data_to_file
from util import get_diff_array


def scan_nita():
    """checks NITA website for new notices

    Returns:
        JSON data of new notices since last run
    """
    filename = 'nita.data.json'
    soup = get_page("https://nita.ac.in/")
    notices = get_links(soup, "notice_board_overflow")
    news = get_texts(soup, "news_card")
    data_new = combine_arrays(notices, news)
    data_prev = read_data_from_file(filename)
    data_diff = get_diff_array(data_new, data_prev)

    if len(data_diff) > 0:
        print("Updates available. Persisting")
        write_data_to_file(filename, data_new)
        body = "\n• ".join(data_diff)

        # for debug builds
        # send_notification(topic="test", title="xNITA Updates", body=body)

        # for production build
        send_notification(topic="release", title="NITA Updates", body=body)

    return jsonify(data=data_diff)


def scan_other():
    """placeholder for other website scanners"""

    return jsonify(data={[]})
