from pyquery import PyQuery as pq

HOST = 'https://www.douyu.com/'
IDS = {'TED': 'TED615', 'infi': 'infiwang', 'th000': 'th000', 'fly': 'fly100', }


def main():
    urls = {k: (HOST + v) for k, v in IDS.items()}
    [print(k + ':   ' + getTitle(v)) for k, v in urls.items()]


def getTitle(url):
    doc = pq(url=url)
    return doc('div.headline.clearfix').find('h1').text()


if __name__ == "__main__":
    main()
