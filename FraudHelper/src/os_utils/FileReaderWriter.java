package os_utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

// ${primary_type_name} - имя класса, его подставляет Eclipse. Очень хорошая идея - создавать класс с именем входного и выходного файлов.
// Правда они почти всегда с маленькой буквы, но это можно пережить.

public class FileReaderWriter implements Runnable {
	StringTokenizer st; // Этот класс быстро умеет разбивать скормленную ему строчку. По умолчанию разделитель - пробел
	BufferedReader in; // Этот класс ОЧЕНЬ быстро умеет считывать данные из какого-либо потока
	PrintWriter out; // Этот класс умеет писать данные, тоже быстро.
	
	private String filePath;
	private String lineDelimeter; // needed for split
	
	public List<String[]> readData = new ArrayList<String[]>();
	
	public FileReaderWriter(String filePath) {
		this.filePath = filePath;
	}
	public FileReaderWriter(String filePath, String lineDelim) {
		this.filePath = filePath;
		this.lineDelimeter = lineDelim;
	}
	
	public static void main(String[] args) {		
		List<String[]>  res = FileReaderWriter.readFile(args[0]);
		for(int i= 0; i < res.size(); i++) {
			System.out.println(res.get(i));
		}
	}
	
	public static List<String[]> readFile(String fileName) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			
			Scanner scanner =
				new Scanner(new FileInputStream(fileName));
			scanner.useDelimiter
				(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				String line = scanner.next();				
				result.add(line.split(" "));
				}
			scanner.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}
		return result;
	}
	
	public static List<String[]> readFile(String fileName, String lineDelim) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			
			Scanner scanner =
				new Scanner(new FileInputStream(fileName));
			scanner.useDelimiter
				(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				String line = scanner.next(); 
				result.add(line.split(lineDelim));
				}
			scanner.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}
		return result;
	}
	
	public static List<String> readlines(String fileName) {
		List<String> result = new ArrayList<String>();
		try {
			
			Scanner scanner =
				new Scanner(new FileInputStream(fileName));
			scanner.useDelimiter
				(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				String line = scanner.next(); 
				result.add(line);
				}
			scanner.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}
		return result;
	}
	
	public void run() {
		Scanner scanner = null;
		try {			
			scanner =
				new Scanner(new FileInputStream(this.filePath));
			scanner.useDelimiter
				(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				String line = scanner.next(); 
				this.readData.add(line.split(" "));
				}
			scanner.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}finally {
					scanner.close();
			}		
	}
	public List<String[]> read() {
		Scanner scanner = null;
		try {	
			
			scanner =
				new Scanner(new FileInputStream(this.filePath));
			//			scanner.useDelimiter
			//	(System.getProperty("line.separator"));
			//scanner.useDelimiter("a");
			System.out.println(scanner.hasNext());
			while (scanner.hasNext()) {
				String line = scanner.next(); 
				this.readData.add(line.split(" "));
				}
			
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}finally {
					scanner.close();
			}
		return this.readData;
	}
	
	public List<String[]> read(String separator) {
		Scanner scanner = null;
		try {			
			scanner =
				new Scanner(new FileInputStream(this.filePath));
			System.out.println("Separator: " + System.getProperty("line.separator"));
			scanner.useDelimiter
				(System.getProperty("line.separator"));
			System.out.println(scanner.hasNext());
			while (scanner.hasNext()) {
				String line = scanner.next(); 
				
				this.readData.add(line.split(separator));
				}
						
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				}finally {					
					scanner.close();
			}
		return this.readData;
	}
  


//  public void run() {
//    try {
//      in = new BufferedReader(new FileReader("${primary_type_name}.in"));
//      out = new PrintWriter(new FileWriter("${primary_type_name}.out"));
//      solve();
//    } catch (Exception e) {
//      //Если есть исключение - выходим с кодом ошибки. Вместо 9000 может быть что угодно, не равное нулю.
//      System.exit(9000);
//    } finally {
//      //Не забываем про закрытие файла.
//      out.flush();
//      out.close();
//    }
//  }
  //Получаем следующий токен
  String nextToken() throws IOException {
    while (st == null || !st.hasMoreTokens()) {
      st = new StringTokenizer(in.readLine());
    }
    return st.nextToken();
  }
  //Следующее 32-битное целое число
  int nextInt() throws NumberFormatException, IOException {
    return Integer.parseInt(nextToken());
  }
  //...
  long nextLong() throws NumberFormatException, IOException {
    return Long.parseLong(nextToken());
  }
  //... Что бы вы думали?
  double nextDouble() throws NumberFormatException, IOException {
    return Double.parseDouble(nextToken());
  }

  void solve() throws NumberFormatException, IOException {
    //А тут уже пишем решение задачи.
  }
}