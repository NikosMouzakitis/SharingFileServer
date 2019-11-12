import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

// client program
public class Client
{
	
	public Client(String serverIP, int sport, String path) {
		this.serverIP = serverIP;
		this.sport = sport;
		this.path = path;	
	}


	public void initialize() {
		
		System.out.println("Sharing the folder: " + path);
		System.out.println("ConTo: "+ this.serverIP + " LclPort: " + sport);

		socket = null;
		pw = null;
		br = null;
		// the bri is for user input, br is used for sending requests throught socket.
		bri = new BufferedReader(new InputStreamReader(System.in));

		//creation of the files that will be shared.
		listOfFiles = getFileNames(path);

		try {
			socket = new Socket(serverIP, sport);
			pw = new PrintWriter(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void signIN() {
		signin = "Signin "+listOfFiles;
		// sends the message over the socket.
		pw.println(signin);
		pw.flush();
		System.out.println("Declaring files..");

		//wait for the Ok reply.
		String okrep = "";
		String dm = "";
		try {
			okrep = br.readLine();
		} catch(IOException e) {
			e.printStackTrace();
		}

		char[] tmp = okrep.toCharArray();

		if( (tmp[0] == 'O') && (tmp[1] == 'k') ) {
			System.out.println("Declared succeed.");
		} else {
			System.out.println("Failed..");
			System.exit(-1);
		}

	}

	public void searchMode() {

		while (true)
		{
			System.out.println("enter keywords-comma seperated: ");

			srep = "";

			try {
				data = bri.readLine();       //reads line.
				tmpr = data.toCharArray();	// data to character arrau.
				

				//checking if this was the signout command
				if(tmpr[0] == 's' && tmpr[1] =='i' && tmpr[2]=='g' && tmpr[3]=='n' && tmpr[4] == 'o' && tmpr[5]=='u' && tmpr[6] == 't') {
					dt2 = "signout";
					System.out.println(dt2);
					pw.println(dt2);
					pw.flush();
					break;	
				}
				
				//creation of message to be sent.
				dt2 = "search "+data;
				System.out.println(dt2);
				pw.println(dt2);
				pw.flush();
				str = "";

				//read the reply from the Server.
				srep = br.readLine(); /// to flush the empty line :)
				srep = br.readLine();
				System.out.println("---RESULTS---");
				freply = srep.substring(7); // prints out the Servers reply.
				stoken = new StringTokenizer(freply, ",");	//creation of the tokens to be printed separated by comma.	
				while(stoken.hasMoreTokens())
					System.out.println(stoken.nextToken());	
				System.out.println("-------------");

			} catch (IOException e)	{
				e.printStackTrace();
			}

		}

	}

	public static String getFileNames(String path)
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		String s = "";
		System.out.println("INSIDE");
		System.out.println(listOfFiles);

		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].isFile())
				s += listOfFiles[i].getName()+",";
		return s.substring(0, s.length()-1);
	}

	public static void main(String[] args)
	{

		if (args.length != 3) {
			System.out.println("Error: you forgot <server IP> <port>");
			return;
		}

		serverIP = args[0];
		sport = Integer.decode(args[1]);
		path = args[2];
		c = new Client(serverIP, sport, path);
		c.initialize();  //initializing the connection.
		c.signIN();	// signin
		c.searchMode();	//functionality of search and getting Server's replies.

	}

	private static   Client c;	
	private  static String str, data, dt2, serverIP, path, srep, listOfFiles, signin;
	private  static int sport, ttt;
	private  Socket socket;
        public static  PrintWriter pw;
	private static String freply;
	private  BufferedReader br, bri;
	private  StringTokenizer stoken;
	private char[] tmpr;	
}
