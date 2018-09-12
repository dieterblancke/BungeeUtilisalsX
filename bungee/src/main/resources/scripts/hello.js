function hello() {
	var name = isConsole() ? "CONSOLE" : user.getName();
	
	return "&fHello &b" + name + "&f, how are you doing?";
}

hello();