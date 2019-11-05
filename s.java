import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.StringTokenizer;

class ServerThread extends Thread
{
	Socket s;
	
	public static ArrayList flist; //files
	public static ArrayList ilist; //ip
	public static ArrayList plist; //port
	private static Object mutex;
	public static int connected = 0;
	public int delindx;

	public ServerThread(Socket s)
	{
		this.s = s;
		System.out.println("Accepted Connection");

	}

	public static void printFlist()
	{

		for(int i = 0; i < flist.size(); i++)
			System.out.println(flist.get(i));

	}
	public void run()
	{

		int passSignin = 0;
		int incomingPort;
		String incomingIP;	

		System.out.print("Connected : ");
		System.out.println(connected);

		System.out.print("Connection from: ");
		System.out.print(s.getInetAddress()+" / ");
		System.out.println(s.getPort());
		incomingPort = s.getPort();
		incomingIP = s.getInetAddress().toString();
		
		System.out.println("Flist: ");
		printFlist();	
		System.out.println("Flist-- ");

		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);

			while (true)
			{
				System.out.println("bf rdLn");	
				String message = br.readLine();

				// client abnormal termination.
				if(message == null) {
					System.out.println("ClientCrash terminating connection");
					break;	
				}

				System.out.println("[dbg] : run");
				System.out.println(message);
		
				// checking if signed up.
					
				if( passSignin == 0) {

					char[] tmp = message.toCharArray();

					if( (tmp[0] == 'S') && (tmp[1] == 'i') && (tmp[2] == 'g') && (tmp[3] == 'n') && (tmp[4] == 'i') && (tmp[5] == 'n') && (tmp[6] == ' ') ) {

						synchronized(mutex) {
							System.out.println("Signin message received");
							String toAddShared = message.substring(7);
							System.out.print("toAddShared: ");
							System.out.println(toAddShared);
							ilist.add(incomingIP);
							plist.add(incomingPort);
							flist.add(toAddShared);	
							connected += 1;
							//send Ok message back to client.
							String okrp = "Ok\n";
							pw.println(okrp);
							pw.flush();
							System.out.println("Sended ok after Declaration of files..");
							passSignin = 1;
						}

					}
				} else { // search functionality performed by the Server.

					char[] tmp = message.toCharArray();
						
					if( (tmp[0] == 's') && (tmp[1] == 'e') && (tmp[2] == 'a') && (tmp[3] == 'r') && (tmp[4] == 'c') && (tmp[5] == 'h') && (tmp[6] == ' ') ) {
						
						String tosearch = message.substring(7);
						System.out.println(tosearch);
						StringTokenizer st = new StringTokenizer(tosearch,","); 
						ArrayList keywords = new ArrayList();

						while(st.hasMoreTokens()) 
							keywords.add(st.nextToken());
						
						for( int i = 0; i <  keywords.size(); i++)
							System.out.println(keywords.get(i));

						// perform search over the declared shared files among the connected users.
						String searchReply = "Results";
						//String searchReply = "Results\n";
					
						for( int i = 0; i < connected; i++) {

							StringTokenizer fst = new StringTokenizer(flist.get(i).toString(),",");	
							// simple search with one keyword.
							ArrayList tmfiles = new ArrayList();
							// pass files of each index to tmfiles for contain check afterwards.
							while(fst.hasMoreTokens())
								tmfiles.add(fst.nextToken());
							
							for(int j = 0; j < tmfiles.size(); j++) 
								if(tmfiles.get(j).toString().contains(keywords.get(0).toString()) == true) {
									searchReply = searchReply + tmfiles.get(j).toString() + ":" + ilist.get(i).toString() + ":" + plist.get(i).toString() + ",";
								//	pw.println(searchReply);
								//	pw.flush();
								}
						}
						// reply to the client.
						searchReply = searchReply + "\n";
						//searchReply = "This is reply.";
						pw.println(searchReply);
						pw.flush();
						System.out.println("Replied to SearchRequest");
						System.out.println(searchReply);

					} else { //else if signout message.

						if( (tmp[0] == 's') && (tmp[1] == 'i') && (tmp[2] == 'g') && (tmp[3] == 'n') && (tmp[4] == 'o') && (tmp[5] == 'u') && (tmp[6] == 't') ) {
							
							//synchronized(mutex) {
								System.out.println("Signout message");		///// TO-DO here remove from lists.
								//get index to remove from all lists.	
							
								for(int i = 0; i < connected; i++)
									if( (int) plist.get(i) == incomingPort)
										delindx = i;	
								
								plist.remove(delindx);
								ilist.remove(delindx);
								flist.remove(delindx);
								connected -= 1;				
						//	}					

						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		
		flist = new ArrayList();
		ilist = new ArrayList();
		plist = new ArrayList();
		
		System.out.println("Server initialized");	
		ServerSocket serverSocket = null;
		mutex = new Object();

		try {
			serverSocket = new ServerSocket(4242);
			System.out.println("Created socket 4242");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		while (true)
		{
			System.out.println("Server Runs");
			Socket clientSocket = null;

			try
			{
				clientSocket = serverSocket.accept();
				new ServerThread(clientSocket).start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}

}

