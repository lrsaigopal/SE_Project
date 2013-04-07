'''
Created on 31-Mar-2013

@author: Sai Gopal
'''
import smtplib
from smtplib import SMTPAuthenticationError,  SMTPException,SMTPRecipientsRefused

def Get_Admin_Pass():
    ''' This Fuction Will Get Email And Pass 
     From DB And Return Them.
    '''
    
    return 'pesitplacementofficer@gmail.com','hiddden'



def Send_Mail(LiEm,Sub,Mess):
    '''  This Function Will Send A Mail To 
        All Email Ids Present In 'LiEm' , 'Sub' as Subject and 'Mess' As The Body
        
        returns Success/Error 
        '''
    
    
    username,password = Get_Admin_Pass()
    fromemail = username
    message = 'Subject: %s\n\n%s' % (Sub, Mess)
  
    try:  
        server = smtplib.SMTP('smtp.gmail.com:587')
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(username,password)
        server.sendmail(fromemail, LiEm,message)
        server.quit()
    except SMTPRecipientsRefused:
        pass #What to do if one of the students emailid is wrong???
    except SMTPAuthenticationError:
        return 'Authentication Error'
    except SMTPException:
        return 'Network Error'
    
    return 'Success'
if __name__ == '__main__':
    print(Send_Mail([''], 'Test Sub','Test Body'))