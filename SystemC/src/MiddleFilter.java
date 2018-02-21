import java.nio.ByteBuffer;
import java.util.ArrayList;

/******************************************************************************************************************
* File:MiddleFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating filters. The details of threading, filter connections, input, and output
* are contained in the FilterFramework super class. In order to use this template the program should rename the class.
* The template includes the run() method which is executed when the filter is started.
* The run() method is the guts of the filter and is where the programmer should put their filter specific code.
* In the template there is a main read-write loop for reading from the input port of the filter and writing to the
* output port of the filter. This template assumes that the filter is a "normal" that it both reads and writes data.
* That is both the input and output ports are used - its input port is connected to a pipe from an up-stream filter and
* its output port is connected to a pipe to a down-stream filter. In cases where the filter is a source or sink, you
* should use the SourceFilterTemplate.java or SinkFilterC.java as a starting point for creating source or sink
* filters.
*
* Parameters: 		None
*
* Internal Methods:
*
*	public void run() - this method must be overridden by this class.
*
******************************************************************************************************************/

public class MiddleFilter extends FilterFramework
{
	ArrayList<Frame> Merged = new ArrayList<>();
	public static void mergeArrays(ArrayList<Frame> arr1, ArrayList<Frame> arr2, int n1,
								   int n2, ArrayList<Frame> arr3)
	{

		for(int i=0;i< arr1.size()+arr2.size();i++){
			arr3.add(new Frame());
		}
		int i = 0, j = 0, k = 0;

		// Traverse both array
		while (i<n1 && j <n2)
		{
			// Check if current element of first
			// array is smaller than current element
			// of second array. If yes, store first
			// array element and increment first array
			// index. Otherwise do same with second array
			if (arr1.get(i).getId0() < arr2.get(j).getId0()){
				Frame aux = new Frame();
				i++;
				aux.setId0(arr1.get(i).getId0()) ;
				aux.setId1(arr1.get(i).getId1()) ;
				aux.setId2(arr1.get(i).getId2()) ;
				aux.setId3(arr1.get(i).getId3()) ;
				aux.setId4(arr1.get(i).getId4()) ;
				arr3.set(k++,aux);
			}

			else{
				Frame aux = new Frame();
				j++;
				aux.setId0(arr2.get(j).getId0()) ;
				aux.setId1(arr2.get(j).getId1()) ;
				aux.setId2(arr2.get(j).getId2()) ;
				aux.setId3(arr2.get(j).getId3()) ;
				aux.setId4(arr2.get(j).getId4()) ;
				arr3.set(k++,aux);
			}
		}


		// Store remaining elements of first array
		while (i < n1){
			Frame aux = new Frame();
			i++;
			aux.setId0(arr1.get(i).getId0()) ;
			aux.setId1(arr1.get(i).getId1()) ;
			aux.setId2(arr1.get(i).getId2()) ;
			aux.setId3(arr1.get(i).getId3()) ;
			aux.setId4(arr1.get(i).getId4()) ;
			arr3.set(k++,aux);
		}


		// Store remaining elements of second array
		while (j < n2){
			Frame aux = new Frame();
			j++;
			aux.setId0(arr2.get(j).getId0()) ;
			aux.setId1(arr2.get(j).getId1()) ;
			aux.setId2(arr2.get(j).getId2()) ;
			aux.setId3(arr2.get(j).getId3()) ;
			aux.setId4(arr2.get(j).getId4()) ;
			arr3.set(k++,aux);
		}
	}

	public void sendFrame(Frame f){
		byte actualF[] = new byte[60];
		byte[] temp;
		int position_F = 0;

		//inicio por ID = 0 no array de bytes da frame a enviar ----- TIME
		temp = ByteBuffer.allocate(4).putInt(0000).array();

		for(int i=0;i<4;i++){
			actualF[position_F]=temp[i];
			position_F++;
		}

		temp = ByteBuffer.allocate(8).putLong(f.getId0()).array();

		for(int i=0;i<8;i++){
			actualF[position_F] = temp[i];
			position_F++;
		}
		//fim por ID = 0 no array de bytes da frame a enviar ------ TIME

		//inicio por ID = 1 no array de bytes da frame a enviar ----- SPEED
		temp =ByteBuffer.allocate(4).putInt(0001).array();

		for(int i=0;i<4;i++){
			actualF[position_F]=temp[i];
			position_F++;
		}

		temp = ByteBuffer.allocate(8).putLong(f.id1).array();

		for(int i=0;i<8;i++){
			actualF[position_F] = temp[i];
			position_F++;
		}
		//fim por ID = 1 no array de bytes da frame a enviar ---- SPEED


		//inicio por ID = 2 no array de bytes da frame a enviar ----- ALTITUDE
		temp =ByteBuffer.allocate(4).putInt(0002).array();

		for(int i=0;i<4;i++){
			actualF[position_F]=temp[i];
			position_F++;
		}

		temp = ByteBuffer.allocate(8).putLong(f.id2).array();

		for(int i=0;i<8;i++){
			actualF[position_F] = temp[i];
			position_F++;
		}
		//fim por ID = 2 no array de bytes da frame a enviar ---- ALTITUDE

		//inicio por ID = 3 no array de bytes da frame a enviar ----- PRESSURE
		temp =ByteBuffer.allocate(4).putInt(0003).array();

		for(int i=0;i<4;i++){
			actualF[position_F]=temp[i];
			position_F++;
		}

		temp = ByteBuffer.allocate(8).putDouble(f.id3).array();

		for(int i=0;i<8;i++){
			actualF[position_F] = temp[i];
			position_F++;
		}
		//fim por ID = 3 no array de bytes da frame a enviar ---- PRESSURE

		//inicio por ID = 4 no array de bytes da frame a enviar ----- TEMPERATURE
		temp =ByteBuffer.allocate(4).putInt(0004).array();

		for(int i=0;i<4;i++){
			actualF[position_F]=temp[i];
			position_F++;
		}

		temp = ByteBuffer.allocate(8).putLong(f.id4).array();

		for(int i=0;i<8;i++){
			actualF[position_F] = temp[i];
			position_F++;
		}
		//fim por ID = 4 no array de bytes da frame a enviar ---- TEMPERATURE

		for(int i=0;i<position_F;i++){
			WriteFilterOutputPort(actualF[i]);			// envia byte a byte a frame toda
		}
	}

	public void run()
    {

		byte databyteA = 0;
		byte databyteB = 0;
		int idA=0, idB=0;
		long measurementA =0L, measurementB=0L;
		byte array[] = new byte[72];
		byte[] temp;
		int position = 0;

		ArrayList<Frame> FramesA = new ArrayList<>();
		ArrayList<Frame> FramesB = new ArrayList<>();


		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream

		byte databyte = 0;				// This is the data byte read from the stream
		int bytesread = 0;				// This is the number of bytes read from the stream
		int byteswritten = 0;				// Number of bytes written to the stream.

		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		int i;							// This is a loop counter
		int sourceA = 0;
		int sourceB = 1;
		int currentSource=0;
		Frame actual=null;
		while (true)
		{

/***************************************************************
*	The program can insert code for the filter operations
* 	here. Note that data must be received and sent one
* 	byte at a time. This has been done to adhere to the
* 	pipe and filter paradigm and provide a high degree of
* 	portabilty between filters. However, you must reconstruct
* 	data on your own. First we read a byte from the input
* 	stream...
***************************************************************/

			try
			{


					id =0;
					// como foi do SourceA temos de consumir bytes do SourceA
					for (i=0; i<IdLength; i++ ){
						databyte = ReadFilterInputPort(currentSource);	// This is where we read the byte from the stream...
						// 0-> SourceA 1-> SourceB
						id = id | (databyte & 0xFF);		// We append the byte on to ID...

						if (i != IdLength-1)				// If this is not the last byte, then slide the
						{									// previously appended byte to the left by one byte
							id = id << 8;					// to make room for the next byte we append to the ID

						} // if

						bytesread++;						// Increment the byte count

						//WriteFilterOutputPort(databyteA); // junçao das 2 streams
						//byteswritten++;

					} // for

					measurement = 0;

					for (i=0; i<MeasurementLength; i++ )
					{
						databyte = ReadFilterInputPort(currentSource);
						measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

						if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
						{												// previously appended byte to the left by one byte
							measurement = measurement << 8;				// to make room for the next byte we append to the
							// measurement
						} // if

						bytesread++;									// Increment the byte count


					} // if
				if(id == 0){
						actual = new Frame();
						actual.setId0(measurement);
				}
				if(id == 1){
					actual.setId1(measurement);
				}
				if(id==2){
					actual.setId2(measurement);
				}
				if(id==3){
					actual.setId3(measurement);
				}
				if(id==4){
					actual.setId4(measurement);

				}
				if(id==5){
					if(currentSource==0){
						FramesA.add(actual);
						currentSource=1;
						actual=null;
					}
					else{
						FramesB.add(actual);
						currentSource=0;
						actual=null;
					}
				}

/***************************************************************
*	Here we could insert code to operate on the input stream...
*  	Then we write a byte out to the output port.
***************************************************************/



			} // try

/***************************************************************
*	When we reach the end of the input stream, an exception is
* 	thrown which is shown below. At this point, you should
* 	finish up any processing, close your ports and exit.
***************************************************************/

			catch (EndOfStreamException e)
			{
				mergeArrays(FramesA, FramesB, FramesA.size(), FramesB.size(), Merged);
				for(i=0;i<Merged.size();i++){
					sendFrame(Merged.get(i));
				}
				try{		// quando enviamos tudo de um source falta enviar o resto do outro

					if(currentSource == 1){		// se o sourceB acabou, enviamos tudo o que falta do sourceA

						array = new byte[72];

						position = 0;

						temp =ByteBuffer.allocate(4).putInt(idA).array();

						for(int a=0;a<4;a++){
							array[position]=temp[a];
							position++;
						}

						temp = ByteBuffer.allocate(8).putLong(measurementA).array();

						for(int a=0;a<8;a++){
							array[position] = temp[a];
							position++;
						}

						for(int a=0;a<position;a++){		// enviar o ID 0 + measurement do id0 que estavam guardados
							WriteFilterOutputPort(array[a]);
							byteswritten++;
						}


						while(true){						// enviar o resto da informacao da stream A

							idA =0;

							for (i=0; i<IdLength; i++ ){
								databyteA = ReadFilterInputPort(0);	// This is where we read the byte from the stream...

								//bytesreadA++;						// Increment the byte count

								WriteFilterOutputPort(databyteA);
								byteswritten++;

							} // for



							measurementA = 0;

							for (i=0; i<MeasurementLength; i++ )
							{
								databyteA = ReadFilterInputPort(0);
								//bytesreadA++;									// Increment the byte count

								WriteFilterOutputPort(databyteA); // junçao das 2 streams
								//byteswritten++;

							} // if


						}

					}else{							// se o sourceA acabou, enviamos tudo o que falta do sourceB

						array =  new byte[72];

						position = 0;

						temp =ByteBuffer.allocate(4).putInt(idB).array();

						for(int a=0;a<4;a++){
							array[position]=temp[a];
							position++;
						}

						temp = ByteBuffer.allocate(8).putLong(measurementB).array();

						for(int a=0;a<8;a++){
							array[position] = temp[a];
							position++;
						}

						for(int a=0;a<position;a++){		// enviar o ID 0 + measurement do id0 que estavam guardados em memoria
							WriteFilterOutputPort(array[a]);
							byteswritten++;
						}

						while(true){

							idB = 0;

							for (i=0; i<IdLength; i++ )
							{
								databyteB = ReadFilterInputPort(1);	// This is where we read the byte from the stream...

								//bytesreadB++;						// Increment the byte count

								WriteFilterOutputPort(databyteB);
								byteswritten++;

							} // for

							measurementB = 0;

							for (i=0; i<MeasurementLength; i++ )
							{
								databyteB = ReadFilterInputPort(1);
								//bytesreadB++;									// Increment the byte count

								WriteFilterOutputPort(databyteB); // junçao das 2 streams
								byteswritten++;

							} // if

						}

					}

				}catch (EndOfStreamException ee){
					ClosePorts();
					//System.out.println( "\n" + this.getName() + "::Middle Filter Exiting; bytes read: " + (bytesreadA+bytesreadB) + " bytes written: " + byteswritten  );
					break;
				}

			} // catch

		} // while

   } // run

} // MiddleFilter