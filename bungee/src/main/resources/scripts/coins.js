function addCoins() {
    var name = isConsole() ? "CONSOLE" : user.getName();
    var coins = storage.getInteger('coins_' + name, 0) + (isConsole() ? 1 : 2);

    storage.set('coins_' + name, coins);
    storage.save();

    return "&rYou now have &b" + coins + " &rcoins";
}

addCoins();