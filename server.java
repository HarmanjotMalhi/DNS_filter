import java.net.*;
import java.io.*;
import java.util.ArrayList;
public class server {
    public static void main(String[] args) throws IOException {

        int port = 53566; // Choose a port number
        DatagramSocket datagramSocket = new DatagramSocket(port);

        byte[] receiveData = new byte[1024]; //used for storing dig incoming packet data
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); //used to save dig packet

        String ipAddress = "50.28.52.163";
        //Used to check for blacklisted ip address
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        byte[] blacklisted1 = inetAddress.getAddress();

        String ippAddress = "139.57.100.6";
        //Uses this to replace the blacklisted IP address
        InetAddress ineetAddress = InetAddress.getByName(ippAddress);
        byte[] ipAddressBytes = ineetAddress.getAddress();


        while (true) {
            datagramSocket.receive(receivePacket);
            //Receive a data packet from dig(port 53566) and put it in receivePacket

            int pr = receivePacket.getPort();
            System.out.println("received from dig");

            DatagramSocket clientSocket = new DatagramSocket();
            //Uses this socket to communicate to google dns server

            InetAddress IPAddress = InetAddress.getByName("8.8.8.8");


            InetAddress host = InetAddress.getByName("localhost");
            //Getting the localhost address

            receivePacket.setAddress(IPAddress);
            //changing the ip address of the packet received from dig to ip address of google dns
            receivePacket.setPort(53);
            //changing the port number of the packet received from dig to default google dns port
            clientSocket.send(receivePacket);
            //Sending that packet

            System.out.println("sent to google dns");

            byte[] receiveFromDns = new byte[1024];
            DatagramPacket receivePacke =new DatagramPacket(receiveFromDns, receiveFromDns.length);
            //Receiving packet from google dns
            clientSocket.receive(receivePacke);

            System.out.println("received response packet from dns");

            boolean check = true;
            int index = 39;
            int i = 0;
            for(int h = 0; h < 4; h++){
                //This whole block of code just checks for the blacklisted ip
                if(blacklisted1[h] == receiveFromDns[index]){
                    i++;
                }
                index++;
            }
            if(i != 4) {check = false;}




            if(check == false){
                //No blacklisted IP address found
                receivePacke.setAddress(host);
                receivePacke.setPort(pr);
                //Setting the socket to where the dig is listening for response
                datagramSocket.send(receivePacke);
            }
            else{
                //Blacklisted IP address found and changed it to brocku.ca ip address and
                //sended it to the socket where dig is listening
                receiveFromDns[39]=ipAddressBytes[0];
                receiveFromDns[40]=ipAddressBytes[1];
                receiveFromDns[41]=ipAddressBytes[2];
                receiveFromDns[42]=ipAddressBytes[3];

                receivePacke =new DatagramPacket(receiveFromDns, receiveFromDns.length);

                receivePacke.setAddress(host);
                receivePacke.setPort(pr);
                datagramSocket.send(receivePacke);

            }


            System.out.println("sent to dig");
        }
    }
}


