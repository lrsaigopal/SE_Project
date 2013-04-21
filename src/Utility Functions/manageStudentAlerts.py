#	Author : Manoj E.
#	Created On : 20 Apr 2013
#	Modified On : 20 Apr 2013

#	The following Python function
#	is used by the Student user to
#	manage alerts. An alert here means
#	that the Student wishes to be notified
#	whenever a particular company comes
#	to the college for placements.

#	The following import is essential
#	for interacting with the MySQL database
#	using Python program.

import MySQLdb;

#	This function is used to manage alerts
#	Argument 1 : is an array consisting
#		     of list of companies that the
#		     Student wishes to be notified of.
#	Argument 2 : is a unique identifier for the Student
#		     who is accessing the database

def manageStudentAlerts(listOfCompanies,usn):
	try:
		flag = False;
		db = MySQLdb.connect("localhost","root","manoj","student_preferences");	# Connect to database
		cursor = db.cursor();							# Create cursor object to interact with database
		num = 1;								# To keep count of number of companies
		for everyCompany in listOfCompanies:
			sqlQuery = "UPDATE STUD_PREFERENCES SET COMPANY" + str(num) + " = \'" + everyCompany + "\' WHERE USN = \'" + usn + "\'";
			num = num + 1;
			cursor.execute(sqlQuery);					# Execute query
			flag = True;
		db.commit();								# Commit to database on success
	except:
		db.rollback();								# Rollback in case of error
	finally:
		db.close();								# close database anyway
		return flag;

# Example input follows
arrOfComps = [];
i = 1;
while (i <= 3):
	arrOfComps.append('Company' + str(i));
	i = i + 1;
manageStudentAlerts(arrOfComps,'usn1');
