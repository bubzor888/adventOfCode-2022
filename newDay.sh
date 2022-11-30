if [ -z "$1" ]
then
	echo "Please provide a day. Example: ./newDay 2"
fi

day="day$1"

mkdir "src/main/kotlin/$day"

echo "Copying main.kt template"
sed -e "s/{{day}}/$day/g" "template.kt" > "src/main/kotlin/$day/main.kt"

echo "Creating blank testInput.txt"
touch "src/main/kotlin/$day/testInput.txt"

echo "Retrieving today's puzzle input"
session=`cat session.txt`
curl -k -s --cookie $session "https://adventofcode.com/2021/day/$1/input" -o "src/main/kotlin/$day/input.txt"