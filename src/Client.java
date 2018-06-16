//RESUME DEVELOPMENT FROM LINE 222 (16/06/2018)
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import common.*;

public class Client {
	//Socket object:
	private Socket sock;
	//Application frame object:
	private JFrame fmain;
	//Starting dialog box:
	private JFrame startd;
	
	//IP address field:
	private JTextField ipbox;
	//Port number field:
	private JTextField portbox;
	
	//DataInputStream, to read server data:
	DataInputStream sv_in;
	//DataOutputStream, to send data to server:
	DataOutputStream sv_out;
	//ObjectInputStream, to receive question objects:
	ObjectInputStream question_in;
	
	//Constructor, creates main quiz frame and calls quizStart():
	public Client() {
		//Main application frame:
		fmain=new JFrame();
		fmain.setSize(1200, 1200);
		fmain.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//Open quiz start Dialog:
		this.quizStart();
		//TODO: Create main application frame (in another method)
		
	}
	
	//Create an error message Dialog box:
	private void errBox(String errortext, JFrame parent) {
		//Create error message:
		JDialog er=new JDialog(parent);
		er.setLayout(new GridLayout(2, 1));
		er.setSize(120, 85);
		//Add label:
		er.add(new JLabel(errortext));
		//Create button:
		JButton cl=new JButton("OK");
		cl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				//Dispose error dialog:
				er.dispose();
			}
		});
		//Add button:
		er.add(cl);
		//Make error message visible:
		er.setVisible(true);
	}
	
	//Shows the first Dialog box, allowing the user to enter the IP address and port number of the quiz server. If the connection is successful, the method quizInit() is called.
	private void quizStart() {
		//Dialog box for IP address and port:
		startd=new JFrame();
		startd.setLayout(new GridLayout(3, 2));
		startd.setSize(300, 100);
		startd.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//Initialize TextFields for IP address and port number:
		ipbox=new JTextField(20);
		portbox=new JTextField(20);
		//Button to start connection:
		JButton cbutton=new JButton("Connect");
		cbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sock=new Socket(ipbox.getText(), Integer.valueOf(portbox.getText()));
					throw new Exception("success");
				} catch(UnknownHostException u) {
					errBox("Unknown host: " + ipbox.getText() + ':' + portbox.getText(), startd);
				} catch(IOException i) {
					errBox("I/O Error!", startd);
				} catch(Exception exc) {
					if(exc.getMessage().equals("success")) quizInit(); //NOT FINISHED!
				}
			}
		});
		//Add elements to startd:
		startd.add(new JLabel(" IP address "));
		startd.add(ipbox);
		startd.add(new JLabel(" Port "));
		startd.add(portbox);
		startd.add(new JLabel(""));
		startd.add(cbutton);
		//Make startd visible:
		startd.setVisible(true);
	}
	
	//Initialize all data stores and prepare to start quiz:
	private void quizInit() {
		try {
			//Initialize data streams:
			sv_in=new DataInputStream(sock.getInputStream());
			sv_out=new DataOutputStream(sock.getOutputStream());
			question_in=new ObjectInputStream(sock.getInputStream());
			//Receive READY signal from server:
			if(sv_in.readUTF().equals("READY")) {
				//Send back "READY" to server:
				sv_out.writeUTF("READY");
				//Get number of questions:
				int no_of_questions=Integer.valueOf(sv_in.readUTF());
				if(sv_in.readUTF().equals("START");
				//Show a dialog box, asking if test should be started:
				errBox("Ready to start test.", fmain);
				//Start test:
				sv_out.writeUTF("OK");
				quizMain(no_of_questions);
			}
		} catch(IOException ioe) {
			System.out.println("I/O Error!");
			System.exit(1);
		}
	}
	
	//Start quiz client, takes number of questions as argument:
	private void quizMain(int n) {
		//Question object:
		Question qtemp;
		//Add 5 rows: selector buttons, question content, previous answer (if any), option selector, "Select answer" and submit buttons:
		fmain.setLayout(new GridLayout(5, 1));
		//Is the test active?
		boolean test_active=true;
		//Current question number:
		int curq=0;
		//Create Question content field:
		JTextArea qcontent=new JTextArea(400, 400);
		qcontent.setEditable(false);
		//Previous answer field:
		JTextField prevans=new JTextField(400);
		prevans.setEditable(false);
		//Option selector:
		JDropDown opts=new JDropDown();
		
		//Array to store answers:
		String[] answers=new String[n];
		//Array to store responses:
		String[] responses=new String[n];
		
		//Button panel:
		JPanel bpanel=new JPanel();
		bpanel.setLayout(new GridLayout(1, 2));
		//"Save answer" button:
		JButton saveans=new JButton("Save answer");
		saveans.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Tell the server we're sending a response:
					sv_out.writeUTF("A");
					if(sv_in.readUTF().equals("OK")) {
						//Send question number:
						sv_out.writeUTF(String.valueOf(curq));
						//Wait for response and send submitted answer:
						if(sv_in.readUTF().equals("ANS") {
							sv_out.writeUTF(opts.getSelectedOption());
						}
					}
				} catch(IOException c) {
					errBox("I/O Error!", fmain);
				}
			}
		});
		//Submit button:
		JButton submitbutton=new JButton("Submit");
		submitbutton.addActionListener(new ActionListener() {
			public void actionPerformed() {
				try {
					//Send "Submit" signal to server:
					sv_out.writeUTF("S");
					//Get response from server:
					if(sv_in.readUTF().equals("RESPS")) {
						//Send OK to server:
						sv_out.writeUTF("OK"):
						//Get user responses:
						for(int i=0; i<n; ++i) responses[i]=new String(sv_in.readUTF());
					}
					//Get answers from server:
					if(sv_in.readUTF().equals("SOLS")) {
						//Send OK to server:
						sv_out.writeUTF("OK");
						//Get answers:
						for(int i=0; i<n; ++i) answers[i]=new String(sv_in.readUTF());
					}
					//Get score from server:
					score=Integer.valueOf(sv_in.readUTF());
					
					//Start shutdown:
					if(sv_in.readUTF().equals("BYE")) sv_out.writeUTF("BYE");
					//Close connections:
					sv_in.close();
					sv_out.close();
					question_in.close();
					
					//Kill fmain:
					fmain.dispose();
					//Build result string:
					StringBuilder fin=new StringBuilder();
					fin.append("RESULTS:\nRESPONSE\tANSWER\n");
					for(int i=0; i<n; ++i) fin.append(responses[i] + '\t' + answers[i] + '\n');
					fin.append("Score: " + score);
					//Display results:
					errBox(fin.toString(), null);
				} catch(IOException c) {
					errBox("I/O Error!", fmain);
				}
			}
		});
		
		//Create question button panel and add buttons:
		JButton qbuttons=new JButton[n];
		for(int i=0; //CONTINUE FROM HERE!
	}
	
	//Main method:
	public static void main(String[] args) {
		new Client();
	}
}
