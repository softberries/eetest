rm -Rf /home/kris/Desktop/bosbaza/*
cd /home/kris/Desktop/bosbaza/
wget http://bossa.pl/pub/ciagle/mstock/mstcgl.zip
unzip mstcgl.zip
rm mstcgl.zip

for i in {1..550}
do
        echo "calling web page.."
        curl http://localhost:8080/eetest/hello1
        echo "web page called $i times"
done


echo "finished"
beep

