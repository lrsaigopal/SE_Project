#	Author : Manoj E.
#	Created On : 15 Apr 2013
#	Modified On : 17 Apr 2013
#	Second Modification On : 20 Apr 2013

#	The following Python function is
#	used by the Faculty User to
#	Update his/her profile.

# 	The following import is used
#	for using MySQL in Python program

import MySQLdb;

#	This is the function used
#	to update a faculty's profile.

#	Argument 1 : is a dictionary that holds
#		     the values for all those fields
#		     whose values will be changed
#	Argument 2 : is the unique identifier for
#		     the faculty user

def updateFacultyProfile(changedFields,facID):
	try:
		flag = False;							# Used to identify whether query executed successfully or not
		db = MySQLdb.connect("localhost","root","manoj","faculty");	# Create connection to server
		cursor = db.cursor();						# Create cursor object to execute query
		for key in changedFields:
			newVal = changedFields[key]				# For every changed field get the new value
			sqlQuery = "UPDATE FACULTY_DETAILS SET " + key + " = \'" + newVal + "\' WHERE facid = \'" + facID + "\'";	# Query to be executed
			cursor.execute(sqlQuery);				# Execute the query
			flag = True;
		db.commit();							# Commit to maintain consistency
	except:
		print "Database busy. Try Later!"
		db.rollback();							# Rollback in case of error
	finally:
		db.close();							# Close dataabse in anycase
		return flag;
	
flagVal = updateFacultyProfile({"email":"abc@gmail.com"},"fa001");		# Example call to database function
