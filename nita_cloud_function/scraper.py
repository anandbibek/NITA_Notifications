import re

import requests
import urllib3
from bs4 import BeautifulSoup

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def get_page(page_url):
    """Fetch content of page into BS4"""

    headers = {'User-Agent': 'Mozilla/5.0'}
    page = requests.get(page_url, headers=headers, verify=False, timeout=10)
    return BeautifulSoup(page.content, "lxml")


def get_links(soup, div_class):
    """Function fetching data from <a>.text() in given div"""

    # select elements
    page_notices = soup.find_all("div", class_=div_class)
    new_notices = []

    # Iterate over the selected elements
    for notice in page_notices:
        # Find anchor tags within each element
        for link in notice.find_all('a'):
            new_notices.append(sanitise(link.get_text()))

    print('DIV: [{}] count {}'.format(div_class, len(new_notices)))
    return new_notices


def get_texts(soup, div_class):
    """Function fetching text() from given div"""

    new_notices = []
    page_news = soup.find_all("div", class_=div_class)
    for news in page_news:
        new_notices.append(sanitise(news.get_text()))

    print('DIV: [{}] count {}'.format(div_class, len(new_notices)))
    return new_notices


def sanitise(val):
    """ Cleanup unwanted characters from data"""

    val = val.strip("\n")
    val = val.strip(".")
    cleaned_text = re.sub(r'\n+', ' ', val)
    return cleaned_text


def combine_arrays(*arrays):
    """Combine arrays into json data"""

    combined_array = []
    for array in arrays:
        combined_array.extend(array)
    return combined_array
