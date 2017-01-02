# -*- coding: utf-8 -*-

'''
自动化发送工作日报邮件
From：947986967@qq.com
To：  smujsb@163.com
'''

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
import sys
from datetime import datetime

mail_to_list = ['smujsb@163.com', 'xzpnuist@163.com']
mail_host = 'smtp.qq.com'
mail_user = '947986967'
mail_pass = 'qjkuaxplzzozbfec'
mail_postfix = '@qq.com'

try:
    file_name = sys.argv[1]
except Exception as e:
    print('请指定附件文件名')
    sys.exit(0)

if not os.path.isfile(file_name):
    print('附件不存在')
    sys.exit(0)

def send_mail(to_list, subject, content):
    me = '<' + mail_user + mail_postfix + '>'
    msg = MIMEMultipart()
    msg['Subject'] = subject
    msg['From'] = me
    msg['To'] = ';'.join(to_list)

    part = MIMEBase('application', 'octet-stream')
    part.set_payload(open(file_name, 'rb').read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', 'attachment; filename=' + file_name)
    msg.attach(part)

    try:
        server = smtplib.SMTP_SSL()
        server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, to_list, msg.as_string())
        server.close()
        return True
    except Exception as e:
        raise e

mail_subject = 'android-徐志平-' + datetime.now().strftime('%Y%m%d')
send_mail(mail_to_lisr, mail_subject, 'android开发徐志平的工作日报')
print('''邮件发送成功:
主题：{0}
附件：{1}'''.format(mail_subject, file_name))
