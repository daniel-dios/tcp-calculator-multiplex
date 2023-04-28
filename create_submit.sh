if [ $# -eq 0 ]; then
  folder="entrega"
else
  folder=$1
fi

cp -r client/src/main/java/ "$folder"
cp -r server/src/main/java/ "$folder"

zip -r "$folder.zip" "$folder"
rm -r "$folder"
