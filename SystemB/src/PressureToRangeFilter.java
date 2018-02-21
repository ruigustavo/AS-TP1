import java.nio.ByteBuffer;
import java.util.ArrayList;

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

public class PressureToRangeFilter extends FilterFramework
{
    ArrayList<Frame> invalidFrames = new ArrayList<>();

    public void updateFrames(Double validPressure,Double lastValidPressure){ //

        for(int i=0;i<invalidFrames.size();i++){ // altera todas as frames invalidas com a media do ultimo valido e o encontrado
            if(lastValidPressure == 0.0){
                invalidFrames.get(i).setId3(-validPressure);
            }
            else{
                invalidFrames.get(i).setId3(-((lastValidPressure+validPressure)/2));
            }
        }
    }

    public void sendFrame(Frame f){
        byte actualF[] = new byte[48];
        byte[] temp;
        int position = 0;

        //inicio por ID = 0 no array de bytes da frame a enviar ----- TIME
        temp =ByteBuffer.allocate(4).putInt(0000).array();

        for(int i=0;i<4;i++){
            actualF[position]=temp[i];
            position++;
        }

        temp = ByteBuffer.allocate(8).putLong(f.getId0()).array();

        for(int i=0;i<8;i++){
            actualF[position] = temp[i];
            position++;
        }
        //fim por ID = 0 no array de bytes da frame a enviar ------ TIME

        //inicio por ID = 2 no array de bytes da frame a enviar ----- ALTITUDE
        temp =ByteBuffer.allocate(4).putInt(0002).array();

        for(int i=0;i<4;i++){
            actualF[position]=temp[i];
            position++;
        }

        temp = ByteBuffer.allocate(8).putLong(f.id2).array();

        for(int i=0;i<8;i++){
            actualF[position] = temp[i];
            position++;
        }
        //fim por ID = 2 no array de bytes da frame a enviar ---- ALTITUDE

        //inicio por ID = 3 no array de bytes da frame a enviar ----- PRESSURE
        temp =ByteBuffer.allocate(4).putInt(0003).array();

        for(int i=0;i<4;i++){
            actualF[position]=temp[i];
            position++;
        }

        temp = ByteBuffer.allocate(8).putDouble(f.id3).array();

        for(int i=0;i<8;i++){
            actualF[position] = temp[i];
            position++;
        }
        //fim por ID = 3 no array de bytes da frame a enviar ---- PRESSURE

        //inicio por ID = 4 no array de bytes da frame a enviar ----- TEMPERATURE
        temp =ByteBuffer.allocate(4).putInt(0004).array();

        for(int i=0;i<4;i++){
            actualF[position]=temp[i];
            position++;
        }

        temp = ByteBuffer.allocate(8).putLong(f.id4).array();

        for(int i=0;i<8;i++){
            actualF[position] = temp[i];
            position++;
        }
        //fim por ID = 4 no array de bytes da frame a enviar ---- TEMPERATURE

        for(int i=0;i<position;i++){
            WriteFilterOutputPort(0,actualF[i]);			// envia byte a byte a frame toda
        }
    }



    public void run()
    {
        System.out.print( "\n" + this.getName() + "::PRESSURE TO RANGE FILTER Reading ");
        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter



        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream
        int byteswritten = 0;				// Number of bytes written to the stream.

        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
        int IdLength = 4;				// This is the length of IDs in the byte stream
        Frame auxFrame=null;
        Double validPressure;
        boolean valid=true;

        Double lastValidPressure = 0.0;
        while (true) {
            try {
                id = 0;

                for (i = 0; i < IdLength; i++) {
                    databyte = ReadFilterInputPort();    // This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);        // We append the byte on to ID...

                    if (i != IdLength - 1)                // If this is not the last byte, then slide the
                    {                                    // previously appended byte to the left by one byte
                        id = id << 8;                    // to make room for the next byte we append to the ID

                    } // if

                } // for

                measurement = 0;

                for (i = 0; i < MeasurementLength; i++) {

                    databyte = ReadFilterInputPort();
                    measurement = measurement | (databyte & 0xFF);    // We append the byte on to measurement...

                    if (i != MeasurementLength - 1)                    // If this is not the last byte, then slide the
                    {                                                // previously appended byte to the left by one byte
                        measurement = measurement << 8;                // to make room for the next byte we append to the

                    } // if

                    bytesread++;

                } // for

                if (id == 0) {
                    if (auxFrame != null) {   // ignora a primeira frame
                        if (!valid) {   // se o valor da ultima frame guardada for invalido
                            invalidFrames.add(auxFrame);        // guarda-se no arraylist
                            valid = true;                    // reset da variavel de controlo

                        } else {
                            // se o valor da pressão da ultima frame guardada for valido
                            if (invalidFrames.size() == 0) {    // se nao existirem frames anteriores com valores de pressao invalidos

                                sendFrame(auxFrame); // envia frame
                            } else {

                                validPressure = auxFrame.getId3(); // valor de pressao valido encontrado

                                updateFrames(validPressure, lastValidPressure); // media entre o ultimo valor valido e o novo, alterando todas as frames que estavam invalidas

                                for (Frame f : invalidFrames) {
                                    sendFrame(f);                    //enviar as frames que estavam invalidas
                                }
                                sendFrame(auxFrame);                // enviar a frame actual que é valida
                                invalidFrames.clear();                        // limpar arraylist das frames invalidas
                            }
                            lastValidPressure = auxFrame.getId3();    // guardar o valor de pressao valido
                        }
                    }
                    auxFrame = new Frame();            // nova frame
                    auxFrame.setId0(measurement);        // guarda o valor do id0 --  TIME
                }


                if (id == 2) {
                    auxFrame.setId2(measurement);        // guarda o valor do id2 -- ALTITUDE
                }

                if (id == 3) {
                    //System.out.println("Measurement1 : " + measurement);
                    Double auxP = Double.longBitsToDouble(measurement);
                    //System.out.println("pressure como vem:"+auxP);
                    auxFrame.setId3(auxP);                            // guarda o valor do id3 --- PRESSURE

                    if (auxP < 50 || auxP > 80 ) {
                        valid = false;                    // verifica se o valor de pressao e valido
                    }
                    //System.out.println("Measurement2 : " + measurement)

                    //System.out.print(TimeStampFormat.format(TimeStamp.getTime()) + " ID = " + id + " " + Double.longBitsToDouble(measurement));

                }

                if (id == 4) {
                    auxFrame.setId4(measurement);        // guarda o valor do id4 -- TEMPERATURE
                }

            }//try

            catch (EndOfStreamException e) {
                //tratar da ultima frame lida para os casos em que antes da ultima há invalidos!
                if (!valid) { //CASO A ULTIMA NAO SEJA VALIDA
                    invalidFrames.add(auxFrame);            //  adiciona-se ao arraylist
                    updateFrames(lastValidPressure, 0.0);        // Interpola-se todos os invalidos pelo ultimo valor valido
                    for (Frame f : invalidFrames) {            // envia todas as frames que estavam invalidas
                        sendFrame(f);
                    }
                    invalidFrames.clear();                    // limpa o array

                } else {
                    // como em primeiro le-mos a frame, guardamos e so a seguir tratamos dele,
                    if (invalidFrames.size() == 0) {    // se nao existirem frames anteriores com valores de pressao invalidos

                        sendFrame(auxFrame);        //  quando o ficheiro acaba resta-nos analisar a ultima frame
                    }                                // se valida envia
                    else {
                        validPressure = auxFrame.getId3(); // valor de pressao valido encontrado
                        updateFrames(validPressure, lastValidPressure); // media entre o ultimo valor valido e o novo, alterando todas as frames que estavam invalidas

                        for (Frame f : invalidFrames) {
                            sendFrame(f);                    //enviar as frames que estavam invalidas
                        }
                        sendFrame(auxFrame);                // enviar a frame valida
                        invalidFrames.clear();              // limpa o array
                    }
                }
                ClosePorts();
                break;
                //e.printStackTrace();

            }//catch
        }// while
    } // run

} //