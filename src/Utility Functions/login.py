#	AUTHOR : Manoj E.
#	Created On : 15 Apr 2013
#	Modified On : 15 Apr 2013

#	The following Python function is used
#	to perform login functionality for
#	the user who logs in into the system.

#	The following import is essential in
#	interacting with the database

import MySQLdb;

#	This function takes two arguments.
#	Argument 1 : is the username of the user
#		     who logs in into the system
#	Argument 2 : is the password of the user
#		     logs in into the system

def login(username, password):
	flag = False;
	db = MySQLdb.connect("localhost","root","manoj","userAuthentication");	# Connect to the database
	cursor = db.cursor();							# Create cursor object to execute queries on db
	sqlQuery = "SELECT * FROM USER_DETAILS;"				# Form the query
	try:
		cursor.execute(sqlQuery);					# Execute the query
		resultSet = cursor.fetchall();					# Get the result set
		for everyRow in resultSet:
			if(username == everyRow[0]):
				if(password == everyRow[1]):			# Cross verify username and password
					flag = True;
		db.commit();							# Commit to db
	except:
		print "Database busy. Try Later!"
		db.rollback();							# Rollback.
	db.close();								# Close db anyway.
	return flag;
