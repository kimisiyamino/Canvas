// Code by LevSergeev
// 29.01.2020
// v.1.0	
// p.s. Код можно уменьшить, но пока решил оставить так.

// Task:
// Дана доска размером M ? N клеток. Клетка может находиться в одном из двух состояний: 1 — живая, 0 — мёртвая. Каждая клетка взаимодействует с восемью соседями. Правила таковы:
//	Живая клетка, у которой меньше двух живых соседей, погибает.
//	Живая клетка, у которой два или три живых соседа, выживает.
//	Живая клетка, у которой больше трёх живых соседей, погибает.
//	Мёртвая клетка, у которой три живых соседа, возрождается.
//		Напишите программу, которая будет:
//		— случайным образом генерить стартовое состояние;
//		— уметь получать его из файла (способ выбирается через параметры запуска в консоли);
//		— каждую секунду выводить в консоль новое состояние доски.


// Запуск через консоль:
//	3 параметра =  Количество Шагов, Время обновления в миллисекундах, Параметр 0 = Рандомная генерация  
//									   Параметр 1 = Генерация из файла start.txt 
//
// 		Example: java Canvas 20 1000 1

//	1 параметр = Генерация из файла start.txt, Шаги и время = Default
//
// 		Example: java Canvas 1

//	0 параметров = Стандартные настройки, рандомная генерация
//
//	Example: java Canvas

/*
	Пример файла "start.txt"
	0001000
	0010100
	0001000
	0010100
	0001000
*/


import java.io.*;
import java.util.*;

public class Canvas{
	
	// Default settings
	int N = 30;				// Height
	int M = 30;				// Width
	char dote = '*';		
	char blank = ' ';		
	int[][] boolField;	
	int[][] newBoolField;
	int Steps = 10;			// Steps
	int Time = 1000;		// Screen refresh time
	
	ArrayList<String> rows = new ArrayList<String>();

	
	// main
	public static void main(String[] args) {
		int[] intArgs = new int[args.length];
		for (int i = 0; i < args.length; i++) {
            intArgs[i] = Integer.parseInt(args[i]);
        }
		new Canvas().go(intArgs);
	}
	
	// start program
	public void go(int[] args){
		// Steps, Time in milliseconds, random / start.txt
		if(args.length == 3)
			setUp(args[0], args[1], args[2]);
		// start.txt
		else if(args.length == 1)
			setUp(args[0]);
		// Default, random
		else setUp();
		
		nextStep();
	}
	
	// Defaut random generation
	public void setUp(){
		boolField = new int[N][M];
		System.out.printf("%n%nLaunch with default settings (Random generation): %n    Height: %d%n    Width: %d\n    Steps: %d\n    Time: %d%n", N, M, Steps, Time);
		startGeneration();
	}
	// User generation + user steps, user time
	public void setUp(int steps, int time, int generation){	// Steps, Time in milliseconds, (0 - Default generation, 1 - User File generation (File start.txt))

		this.Steps = steps;
		this.Time = time;
		
		if(generation == 0){
			setUp();
		}else{
			userGeneration();
		}
	}
	// User generation + default steps
	public void setUp(int generation){
		userGeneration();
	}
	
	public void userGeneration(){
		String line;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(new File("start.txt")));
			while((line = reader.readLine()) != null){
				rows.add(line);
			}
			reader.close();
			createField();
		}catch(IOException e){
			e.printStackTrace(); 
			setUp();
		}
		
	}

	public void createField(){
		N = rows.size();
		char[] ch = rows.get(0).toCharArray();
		M = ch.length;
		boolField = new int[N][M];
		
		//create user field
		for(int h = 0; h < N; h++){
			ch = rows.get(h).toCharArray();
			M = ch.length;
			for(int w = 0; w < M; w++){
				boolField[h][w] = Character.getNumericValue(ch[w]);
			}
		}
		//print user field
		for(int i = 0; i < N; i++){
			System.out.print("\n");
			for(int j = 0; j < M; j++){	
				if(boolField[i][j] == 1){
					System.out.print(dote);
				}else{
					System.out.print(blank);
				}
			}
		}	
	}
	
	// first run
	public void startGeneration(){
		int Life;
		// create and print first field
		for(int i = 0; i < N; i++){
			System.out.print("\n");
			for(int j = 0; j < M; j++){
				Life = (int)(Math.random()*2);
				if(Life == 1){
					System.out.print(dote);
					boolField[i][j] = Life;
				}else{
					System.out.print(blank);
					boolField[i][j] = Life;
				}
			}
		}	
	}
	
	public void nextStep(){
		newBoolField = new int[N][M];
		int Count = 0;
		while(Count<Steps){
			Count++;	
			System.out.print("\n");
			// create new field
			for(int i = 0; i < N; i++){
				for(int j = 0; j < M; j++){
					int neighborNum = neighborCount(i,j);
					if(boolField[i][j] == 1 && (neighborNum < 2 || neighborNum > 3)){
						newBoolField[i][j] = 0;
					}
					else if(boolField[i][j] == 1 && (neighborNum == 2 || neighborNum == 3)){
						newBoolField[i][j] = 1;
					}
					else if(boolField[i][j] == 0 && (neighborNum == 3)){
						newBoolField[i][j] = 1;
					}
				}
			}
			// print new field
			try{
				Thread.sleep(Time);
				for(int i = 0; i < N; i++){
					System.out.print("\n");
					for(int j = 0; j < M; j++){
						if(newBoolField[i][j] == 1){
							System.out.print(dote);
							boolField[i][j] = newBoolField[i][j];
						}else{
							System.out.print(blank);
							boolField[i][j] = newBoolField[i][j];
						}
					}
				}
			}catch(InterruptedException ex){ex.printStackTrace();}
		}
	}
	
	// search for neighbors
	public int neighborCount(int i, int j){
		int neighborNum = 0;
		for(int x = i - 1; x < i + 2; x++){
			for(int y = j - 1; y < j + 2; y++){
				if(x < 0 || y < 0 || y > M - 1 || x > N - 1){ continue; }			
				if(x == i && y == j){ continue; }								
				if(boolField[x][y] == 1){ neighborNum++; }							
			}	
		}
		return neighborNum;
	}
}
