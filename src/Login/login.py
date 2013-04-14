import MySQLdb;

def login(username, password):
	flag = False;
	db = MySQLdb.connect("localhost","root","manoj","userAuthentication");
	cursor = db.cursor();
	sqlQuery = "SELECT * FROM USER_DETAILS;"
	try:
		cursor.execute(sqlQuery);
		resultSet = cursor.fetchall();
		for everyRow in resultSet:
			if(username == everyRow[0]):
				if(password == everyRow[1]):
					flag = True;
	except:
		print "Database busy. Try Later!"
	db.close();
	return flag;
