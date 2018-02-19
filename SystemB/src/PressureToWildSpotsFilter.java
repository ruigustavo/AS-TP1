import java.nio.ByteBuffer;

/******************************************************************************************************************
 * File:FarToCelsiusFilter.java
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
 * should use the SourceFilter.java or SinkFilter.java as a starting point for creating source or sink
 * filters.
 *
 * Parameters: 		None
 *
 * Internal Methods:
 *
 *	public void run() - this method must be overridden by this class.
 *
 ******************************************************************************************************************/

public class PressureToWildSpotsFilter extends FilterFramework
{
    public static byte[] convertToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putDouble(value);
        return buffer.array();

    }

    public static byte[] convertToByteArrayv2(int value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putInt(value);
        return buffer.array();

    }

    public void run()
    {

        System.out.print( "\n" + this.getName() + "::MIDDLE FILTER Reading ");
        byte[] output = new byte[8];
        byte[] FinalOutput=null;
        byte[] outputId = new byte[4];
        byte[] temp;
        int finalPos=0;
        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter


        Frame actual = new Frame();
        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream
        int byteswritten = 0;				// Number of bytes written to the stream.

        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
        int IdLength = 4;				// This is the length of IDs in the byte stream
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
                id = 0;

                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);		// We append the byte on to ID...

                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID

                    } // if

                } // for

                measurement = 0;

                    for (i=0; i<MeasurementLength; i++ ){

                        databyte = ReadFilterInputPort();
                        measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

                        if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                        {												// previously appended byte to the left by one byte
                            measurement = measurement << 8;				// to make room for the next byte we append to the

                        } // if

                        bytesread++;

                    } // for
                if (id == 0){  // ID = 0 , DATA
                    FinalOutput = new byte[24]; // Array de cada FRAME
                    finalPos=0; // POSIÇAO EM QUE ESTAMOS NA FRAME
                    temp = ByteBuffer.allocate(4).putInt(0000).array(); // PASSAR DE INTEIRO PARA ARRAY DE BYTE
                    for (i = 0; i< 4; i++){  // FOR -- METE DO ARRAY TEMPORARIO PARA O ARRAY FINAL OS 4 BYTES DO ID
                        FinalOutput[finalPos] = temp[i];
                        finalPos++;
                    }
                    temp = ByteBuffer.allocate(8).putLong(measurement).array(); //PASSAR DE LONG PARA ARRAY DE BYTE
                    for (i = 0; i< 8; i++){ // FOR -- METE DO ARRAY TEMPORARIO PARA O ARRAY FINAL OS 8 BYTES DO MEASUREMENT, NESTE CASO DA DATA
                        FinalOutput[finalPos] = temp[i];
                        finalPos++;
                    }
                }
                if ( id == 3 ) // ID = 3 ---- pressure
                {
                    double aux = (Double.longBitsToDouble(measurement));
                    if (aux < 50 || aux > 80) {
                        temp = ByteBuffer.allocate(4).putInt(0003).array(); //PASSAR DE INTEIRO PARA ARRAY DE BYTE
                        for (i = 0; i < 4; i++){
                            FinalOutput[finalPos] = temp[i];
                            finalPos++;
                        }
                        temp = ByteBuffer.allocate(8).putLong(measurement).array(); //PASSAR DE LONG PARA ARRAY DE BYTE
                        for (i = 0; i < 8; i++){//FOR -- METE DO ARRAY TEMPORARIO PARA O ARRAY FINAL OS 8 BYTES DO MEASUREMENT, NESTE CASO DA PRESSURE
                            FinalOutput[finalPos] = temp[i];
                            finalPos++;
                        }

                        for(i=0;i < finalPos; i++){// envia os 24 bytes 12 da data e 12 da pressure para o proximo filtro
                            WriteFilterOutputPort(FinalOutput[i]);
                        }
                    }

                } // if




            } // try

/***************************************************************
 *	When we reach the end of the input stream, an exception is
 * 	thrown which is shown below. At this point, you should
 * 	finish up any processing, close your ports and exit.
 ***************************************************************/

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.println( "\n" + this.getName() + ":: bytes read::" + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run

} // FarToCelsiusFilter