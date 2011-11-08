for i in {1..1300}
do
	echo "calling web page.."
	curl http://localhost:8080/eetest/hello1
	echo "web page called $i times"
done

