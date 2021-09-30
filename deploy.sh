for moduleName in common bungee spigot velocity; do
  mvn deploy -pl $moduleName -DskipTests
done

echo "Finished deploying BungeeUtilisalsX"
