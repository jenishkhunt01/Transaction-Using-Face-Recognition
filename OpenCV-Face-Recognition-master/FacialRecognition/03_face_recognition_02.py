import cv2
import numpy as np
import os 
import mysql.connector
from mysql.connector import Error
from mysql.connector import errorcode
from datetime import datetime

now = datetime.now()
dt_string = now.strftime("%Y-%m-%d %H:%M:%S")
print(dt_string)

recognizer = cv2.face.LBPHFaceRecognizer_create()
recognizer.read('E:/EPICS Project/OpenCV-Face-Recognition-master/FacialRecognition/trainer/trainer.yml')
cascadePath = "E:/EPICS Project/OpenCV-Face-Recognition-master/FacialRecognition/haarcascade_frontalface_default.xml"
faceCascade = cv2.CascadeClassifier(cascadePath);

font = cv2.FONT_HERSHEY_SIMPLEX

#iniciate id counter
id=0
mydb = mysql.connector.connect(
  host="localhost",
  user="root",
  password="root",
  database="ajbank"
)

Login = input("Enter Login ID: ")
#Login = input()
Password = input("Enter Password: ")
transaction=(int (input("Enter Transaction Amount: ")))
AccountType = int(input("Enter the Account Type you want to withdraw money from\nSelect 1 or 2 as choice\n1. Primary Account\n2. Savings Account\n"))
mycursor = mydb.cursor()
# names related to ids: example ==> Ansh: id=1,  etc
connection = mysql.connector.connect(host='localhost',
                                     database='ajbank',
                                     user='root',
                                     password='root')
cursor = connection.cursor()
cursor.execute("SELECT username from user")
names=['None']
for row in cursor:
    names.append(row[0])
cursor.execute("SELECT confirmpassword from user")
passwords=['None']
for row2 in cursor:
    passwords.append(row2[0])
cursor.execute("SELECT confirmpassword from user where username=%s", (Login,))
authentication=[]
for row3 in cursor:
    authentication.append(row3[0])
#names = ["None","19BCE10052","Swati","Anshuman","19BCE10040","Ayush","Rachit","Shrey","Siddharth",]

# Initialize and start realtime video capture

cam = cv2.VideoCapture(0)
cam.set(3, 640) # set video widht
cam.set(4, 480) # set video height

# Define min window size to be recognized as a face
minW = 0.1*cam.get(3)
minH = 0.1*cam.get(4)
if authentication[0]==Password:
    while True:

        ret, img =cam.read()
        img = cv2.flip(img, 1) # Flip vertically

        gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

        faces = faceCascade.detectMultiScale( 
            gray,
            scaleFactor = 1.2,
            minNeighbors = 5,
            minSize = (int(minW), int(minH)),
           )

        for(x,y,w,h) in faces:

            cv2.rectangle(img, (x,y), (x+w,y+h), (0,255,0), 2)

            id, confidence = recognizer.predict(gray[y:y+h,x:x+w])

            # Check if confidence is less them 100 ==> "0" is perfect match 
            if (confidence < 100):
                PersonID = id
                print(PersonID)
                id = names[id]
                confidence = "  {0}%".format(round(100 - confidence))
                t=1
                
                if id==Login:
                    #put_text(test_img,predict_name,x,y)
                    try:
                        connection = mysql.connector.connect(host='localhost',
                                                             database='ajbank',
                                                             user='root',
                                                             password='root')
                        mySql_insert_query = ("""INSERT INTO face (PersonID,Username) 
                                               VALUES 
                                               (%s, %s) """)
                                               
                        
                        record = (PersonID, id)
                        cursor = connection.cursor()
                        cursor.execute(mySql_insert_query, record)
                        if AccountType==1:
                            cursor.execute("SELECT p.account_balance, p.id from primary_account p LEFT JOIN user u on p.id = u.primary_account_id  where username=%s;", (Login,))
                        else:
                            cursor.execute("SELECT s.account_balance, s.id from savings_account s LEFT JOIN user u on s.id = u.savings_account_id  where username=%s;", (Login,))
                        output =[]
                        for row in cursor:
                            output.append(float(row[0]))
                            output.append(int(row[1]))
                        amount=output[0]-transaction;
                        if AccountType==1:
                            cursor.execute("SELECT max(id) from ajbank.primary_transaction;")
                        else:
                            cursor.execute("SELECT max(id) from ajbank.savings_transaction;")
                        maxid=[]
                        for row in cursor:
                            maxid.append(row[0])
                        insertid=maxid[0]
                        insertid=insertid+1
                        cursor.execute("SELECT primary_account_id FROM user WHERE username=%s;", (Login,))
                        primary_account_id=[]
                        for row in cursor:
                            primary_account_id.append(row[0])
                        cursor.execute("SELECT savings_account_id FROM user WHERE username=%s;", (Login,))
                        savings_account_id=[]
                        for row in cursor:
                            savings_account_id.append(row[0])
                        if AccountType == 1:
                            if amount>0:
                                cursor.execute("UPDATE `ajbank`.`primary_account` SET `account_balance` = %s WHERE (`id` = %s);", (amount, output[1],))
                                cursor.execute("INSERT INTO `ajbank`.`primary_transaction` (`id`,`amount`, `available_balance`, `date`, `description`, `status`, `type`, `primary_account_id`) VALUES (%s, %s, %s, %s, 'Withdrawal From Software', 'Finished', 'Account', %s);", (insertid, transaction, amount, dt_string, primary_account_id[0],))
                            else:
                                print("Insufficient Amount!")
                                cursor.execute("INSERT INTO `ajbank`.`primary_transaction` (`id`,`amount`, `available_balance`, `date`, `description`, `status`, `type`, `primary_account_id`) VALUES (%s, %s, %s, %s, 'Insufficient Balance', 'Finished', 'Account', %s);", (insertid, 0, output[0], dt_string,  primary_account_id[0],))
                            connection.commit()
                        else:
                            if amount>0:
                                cursor.execute("UPDATE `ajbank`.`savings_account` SET `account_balance` = %s WHERE (`id` = %s);", (amount, output[1],))
                                cursor.execute("INSERT INTO `ajbank`.`savings_transaction` (`id`,`amount`, `available_balance`, `date`, `description`, `status`, `type`, `savings_account_id`) VALUES (%s, %s, %s, %s, 'Withdrawal From Software', 'Finished', 'Account', %s);", (insertid, transaction, amount, dt_string, savings_account_id[0],))
                            else:
                                print("Insufficient Amount!")
                                cursor.execute("INSERT INTO `ajbank`.`savings_transaction` (`id`,`amount`, `available_balance`, `date`, `description`, `status`, `type`, `savings_account_id`) VALUES (%s, %s, %s, %s, 'Insufficient Balance', 'Finished', 'Account', %s);", (insertid, 0, output[0], dt_string, savings_account_id[0],))
                            connection.commit()
                        cam.release()
                        print(cursor.rowcount, "Record inserted successfully into Guest table")
                        cursor.close()
                        break
        
                    except mysql.connector.Error as error:
                        print("Failed to insert record into Laptop table {}".format(error))
        
                    finally:
                        if (connection.is_connected()):
                            connection.close()
                            print("MySQL connection is closed")
                            cam.release()
                            break
                else:
                    print("Wrong ID or Password!")
                    cam.release()
                    break;
                        
            else:
                id = "unknown"
                confidence = "  {0}%".format(round(100 - confidence))
            
            cv2.putText(img, str(id), (x+5,y-5), font, 1, (255,255,255), 2)
            cv2.putText(img, str(confidence), (x+5,y+h-5), font, 1, (255,255,0), 1)  
        
        cv2.imshow('camera',img) 

        k = cv2.waitKey(10) & 0xff # Press 'ESC' for exiting video
        if k == 27:
            break

    # Do a bit of cleanup
    #print("\n [INFO] Exiting Program and cleanup stuff")    

    #sql = "INSERT INTO Persons (PersonID, Username) VALUES (%s, %s)"
    #val = ("1", id)
else:
    print("\n Invalid Credentials")

cv2.destroyAllWindows()

