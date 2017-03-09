# -*- coding: utf-8 -*-

'''
自动化发送工作日报邮件
注意：
    用作发送方的邮件账户需开通SMTP服务
'''

import os
import smtplib
import sys
from datetime import datetime
from email import encoders
from email.mime.base import MIMEBase
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText


class Pymail():
    """Sending mail with python3"""
    _server = None
    _mail_host = None
    _mail_user = None
    _mail_pass = None

    _mail_attachments = []

    def __init__(self, mail_host, mail_user, mail_pass, mail_postfix=None):
        self._mail_host = mail_host
        self._mail_user = mail_user
        self._mail_pass = mail_pass
        if mail_postfix is None:
            self._mail_postfix = '@' + mail_host[mail_host.index('.') + 1:]

    def _login(self):
        if self._server is None:
            self._server = smtplib.SMTP_SSL()
        self._server.connect(self._mail_host)
        self._server.login(self._mail_user, self._mail_pass)

    def _logout(self):
        if self._server is not None:
            self._server.close()

    def add_attachments(self, file_list):
        for file_item in file_list:
            if file_item is not None:
                self.add_attachment(file_item)

    def add_attachment(self, file_name):
        part = MIMEBase('application', 'octet-stream')
        part.set_payload(open(file_name, 'rb').read())
        part.add_header('Content-Disposition', 'attachment', filename=('gbk', '', file_name))
        encoders.encode_base64(part)
        self._mail_attachments.append(part)

    def send_mail(self, to_list, subject, cont):
        me = '<' + self._mail_user + self._mail_postfix + '>'
        msg = MIMEMultipart()
        msg['Subject'] = subject
        msg['From'] = me
        msg['To'] = ';'.join(to_list)

        for part in self._mail_attachments:
            msg.attach(part)

        mime_text = MIMEText(cont, 'plain')
        content = mime_text
        msg.attach(content)

        try:
            self._login()
            self._server.sendmail(me, to_list, msg.as_string())
            self._logout()
        except Exception as e:
            raise e


def main():
    try:
        files = sys.argv[1:]
    except Exception as e:
        print('请指定附件文件名')
        sys.exit(0)

    for file in files:
        if not os.path.isfile(file):
            print('当前目录找不到文件：' + file)
            sys.exit(0)

    mail_host = 'smtp.qq.com'
    mail_user = '947986967'
    mail_pass = 'qjkuaxplzzozbfec'
    mail_to_list = ['smujsb@163.com', 'xzpnuist@163.com']
    mail_subject = 'android-徐志平-' + datetime.now().strftime('%Y%m%d')
    mail_content = 'android开发徐志平的工作日报'

    pymail = Pymail(mail_host, mail_user, mail_pass)
    pymail.add_attachments(files)
    pymail.send_mail(mail_to_list, mail_subject, mail_content)

    print('''邮件发送成功:
	主题：{0}
	附件：{1}'''.format(mail_subject, files))


if __name__ == '__main__':
    main()
