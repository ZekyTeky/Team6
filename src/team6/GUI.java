package team6;
/**
 * @author Uwera Ntaganzwa
 */
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class GUI extends JFrame implements ActionListener {
		Cohort cohort;
		private static String fileDictName = "";
		private static String fileName = readInFile();
		private static int parseCount;
		private static int writeCount;
		private static JButton parseButton;
		private static JButton excelButton;
		private static JButton retrieveFilesButton;
		private static JLabel message;
		private static JLabel message2;
		private static JLabel retrieveMessage;
		private static JLabel listedFiles;
		ArrayList<Course> sortedList = new ArrayList<>();
		GUI(){}
	    public static void main(String args[]){
	       JFrame frame = new JFrame("Student Transcript Analyser");
	       JPanel panel = new JPanel();
	       frame.setSize(500,450);
	       frame.add(panel);
	       
	       parseButton = new JButton("Parse Transcripts");
	       parseButton.setBounds(50, 200, 150, 30);
	       panel.add(parseButton);
	       message = new JLabel("No transcripts parsed yet.");
	       panel.add(message);
	       parseCount =0;
	       
	       
	       excelButton = new JButton("Write Results to Excel");
	       message2 = new JLabel("No workbook created yet.");
	       panel.add(excelButton);
	       panel.add(message2);
	       writeCount = 0;
	       excelButton.setVisible(false);
	       message2.setVisible(false);
	       
	       retrieveFilesButton = new JButton("Retrieve Stored Files");
	       retrieveMessage = new JLabel("");
	       listedFiles = new JLabel("");
	       panel.add(retrieveFilesButton);
	       panel.add(retrieveMessage);
	       panel.add(listedFiles);
	       retrieveFilesButton.setVisible(false);
	       retrieveMessage.setVisible(false);
	       listedFiles.setVisible(false);
	       
	       GUI fileChooser = new GUI();
	       parseButton.addActionListener(fileChooser);
	       excelButton.addActionListener(fileChooser);
	       retrieveFilesButton.addActionListener(fileChooser);	       
	       
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       frame.setVisible(true);
	    }
	    
	    public void actionPerformed(ActionEvent e){
	    	 String event = e.getActionCommand(); 

	         if (event.equals("Parse Transcripts")) { 
	            if (parseCount > 0) {
		        	message.setText("Transcripts in this cohort have already been parsed.");
		        	throw new IllegalArgumentException();
		        }
		        try {	 
		             File directory = null;
		             directory = TranscriptReader.getDirectory();
		     			
		             File[] transcriptSet = directory.listFiles();
		     	     System.out.println("Transcript count: " + transcriptSet.length);
		     	     
		     	     LevelSchema.getSchemaConfig();
		     	     cohort = TranscriptReader.parseTranscripts(transcriptSet);
		     	     message.setText(transcriptSet.length + " transcripts successfully parsed.");
		     	     excelButton.setVisible(true);
		     	     message2.setVisible(true);
		     	     //prints to console
		     	     System.out.println(CourseList.printTextRawList());
		     	     //Automatically prints cohorts global to the excel. 
	        		
		     	     AreaList.makeAreaList();
		     	     
		     	     parseCount++; 
		        } catch (IllegalArgumentException e1) {
		        	message.setText("Error parsing transcripts. One or more files may be corrupted.");
		        } catch (IOException e1) {
					e1.printStackTrace();
				}
	         }
	         
	         if(event.equals("Write Results to Excel")){
	        	 if (writeCount > 0) {
			        	message.setText("A results workbook has already been created.");
			        	throw new IllegalArgumentException();
			      }
	        	 
	        	  sortedList = CourseList.getCourseList();
	        	  
	        	  try {

					FileHandler.writeRawList(sortedList, fileName);
					FileHandler.writeMasterList(cohort, fileName);
					FileHandler.writeGlobalDistribution(cohort, fileName);
					FileHandler.writeAreaDistribution(AreaList.getAreas(), fileName);
					message2.setText("Results have been written to an excel workbook.");
					retrieveFilesButton.setVisible(true);
					retrieveMessage.setVisible(true);
					listedFiles.setVisible(true);
				} catch (FileNotFoundException e1) {
					message.setText("File not found.");
					e1.printStackTrace();
				}catch(IllegalArgumentException ia) {
					message.setText("This excel had already been written to. Please delete output sheets or define a new workbook");
				}catch (IOException e1) {
					message.setText("Failed to write to excel.");
					e1.printStackTrace();
				}
	         }
	         
	         if(event.equals("Retrieve Stored Files")){
	        	 retrieveMessage.setText("The following files are available for further processing:\n");
	        	 FileHandler.retrieveStoredFiles();
	        	 
	         }
}
	    
	    private static String readInFile() {
	    	String source = System.getProperty("user.dir");
	    	JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select your input file, that includes Area & the Course Equivalences");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Files", "xlsx");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setSelectedFile(new File(source));
            fileChooser.setVisible(true);
            int result = fileChooser.showOpenDialog(fileChooser);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fileDictName = fileChooser.getSelectedFile().getAbsolutePath();
            } else {
                return "NULL";
            }
            
      	  String name = fileChooser.getSelectedFile().getAbsolutePath();
      	  if (!name.contains(".")) {
      		  name = name + ".xlsx";
      	  }
      	  else if (name.substring(name.lastIndexOf("."), name.length()) != ".xlsx") {
      		  name = name.replace(name.substring(name.lastIndexOf("."), name.length()), ".xlsx");
      		  System.out.println(name);
      	  }
      	  
      	  return name;
	    }
}
