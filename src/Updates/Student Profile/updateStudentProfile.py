import MySQLdb;

def updateStudentProfile(changedFields,usn):
	# We are assuming that the rollback in case of no
	# change to Database will not change the state of Database
	try:
		flag = False;
		db = MySQLdb.connect("localhost","root","manoj","student");
		cursor = db.cursor();
		for key in changedFields:
			newVal = changedFields[key]
			sqlQuery = "UPDATE STUDENT_DETAILS SET " + key + " = " + newVal + " WHERE usn = " + usn + ";";
			cursor.execute(sqlQuery);
			flag = True;
		db.commit();
	except:
		print "Database busy. Try Later!"
		db.rollback();
	finally:
		db.close();
		return flag;
	
flagVal = updateStudentProfile("email","abc@gmail.com","1PI10CSxxx");