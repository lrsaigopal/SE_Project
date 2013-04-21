#	Author : Manoj E.
#	Created On : 15 Apr 2013
#	Modified On : 17 Apr 2013
#	Second Modification On : 20 Apr 2013

#	The following Python function is
#	used by the Stident User to
#	Update his/her profile.

# 	The following import is used
#	for using MySQL in Python program

import MySQLdb;

#	This is the function used
#	to update a student's profile

#	Argument 1 : is a dictionary that holds
#		     the values for all those fields
#		     whose values will be changed
#	Argument 2 : is the unique identifier for
#		     the student user

def updateStudentProfile(changedFields,usn):
	try:
		flag = False;							# Used to identify whether query executed successfully or not.
		db = MySQLdb.connect("localhost","root","manoj","student");	# Used to connect to the database
		cursor = db.cursor();						# Making a cursor object to execute a query
		for key in changedFields:
			newVal = changedFields[key]				# For every changed field, get the new value
			sqlQuery = "UPDATE STUDENT_DETAILS SET " + key + " = \'" + newVal + "\' WHERE usn = \'" + usn + "\'";	# Executable query
			cursor.execute(sqlQuery);				# Execute the query
			flag = True;
		db.commit();							# Commit to database to maintain consistency
	except:
		print "Database busy. Try Later!"
		db.rollback();							# Rollback otherwise
	finally:
		db.close();							# Close the database whatsoever
		return flag;
	
flagVal = updateStudentProfile({"email":"abc@gmail.com"},"1PI10CSxxx");		# Example update student profile call
