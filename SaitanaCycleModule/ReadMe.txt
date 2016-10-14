

SAITANA PERFECTURE CYCLE RENTING APPLICATION:

1)Import this code in android studio, start the node.js server.

2)Go to the backend package in src directory - Open JSONService.java class.
    This class accesses the rest endpoints.

    Modify the url given at top as shown :

    private String url = "http://192.168.43.205:8080/api/v1/";

    replace the "192.168.43.205" with the IPV4 address of your local machine.

    To obtain the IPV4 address open command prompt in your computer and type

    "ipconfig" for Windows OS and "ifconfig" for Linux.


3)I was not able to complete unit test components.

