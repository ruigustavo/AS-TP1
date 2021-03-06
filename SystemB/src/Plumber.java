/******************************************************************************************************************
* File:Plumber.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating a main thread that will instantiate and connect a set of filters.
* The details of the filter operations is totally self contained within the filters, the "plumber" takes care of
* starting the filters and connecting each of the filters together. The details of how to connect filters is taken
* care of by the FilterFramework. In order to use this template the program should rename the class.
* The template includes the RunFilter() method which is executed when the filter is started. While simple, there
* are semantics for instantiating, connecting, and starting the filters:
*
*	Step 1: Instantiate the filters as shown in the example below. You should create the filters using the
*			templates provided, and you must use the FilterFramework as a base class for all filters. Every pipe and
*			filter network must have a source filter where data originates, and a sink filter where the data flow
*			terminates.
*
*	Step 2: Connect the filters. Start with the sink and work backward to the source. Essentially you are connecting
*			the	input of each filter to the up-stream filter's output until you get to the source filter. Filter have
*			a Connect() method which accepts a FilterFramework type. This method connects the calling filter's input
*			to the passed filter's output. Again the example in the comments below illustrates how this is done.
*
*	Step 3: Start the filters using the start() method.
*
*	Once the filters are started this main thread dies and the pipe and filter network processes data until there
* 	is no more data movement from the source. Each filter will shutdown when data is no longer available (provided
*	you followe the read semantics described in the filter templates).
*	on their input ports.
*
* Parameters: 		None
*
* Internal Methods:	None
*
******************************************************************************************************************/
public class Plumber
{
   public static void main( String argv[])
   {
		/****************************************************************************
		* Here we instantiate three filters.
		****************************************************************************/


	   SourceFilter Filter1 = new SourceFilter();	        	// This is a source filter - see SourceFilter.java
	   FarToCelsiusFilter Filter2 = new FarToCelsiusFilter();	// This is a standard filter - see FarToCelsiusFilter.java
	   FeetToMeterFilter Filter3 = new FeetToMeterFilter();		// This is a standard filter - see FeetToMeterFilter.java
	   PressureToRangeFilter Filter4 = new PressureToRangeFilter();			// This is a standard filter - see PressureToRangeFilter.java
	   //PressureToWildSpotsFilter Filter5 = new PressureToWildSpotsFilter();			// This is a standard filter - see PressureToRangeFilter.java
	   SinkFilter Filter5 = new SinkFilter();		        	// This is a sink filter - see SinkFilter.java
	   SinkWildPointsFilter Filter6 = new SinkWildPointsFilter();		// This is a standard filter - see SinkWildPointsFilter.java

		/****************************************************************************
		* Here we connect the filters starting with the sink filter (Filter 1) which
		* we connect to Filter2 the middle filter. Then we connect Filter2 to the
		* source filter (Filter3). You must connect filters starting with the sink
		* filter and working your way back to the source as shown here.
		****************************************************************************/




	   // Filter7.Connect(Filter5); // This essentially says, "connect Filter6's input port to Filter5's output port
	    Filter6.Connect(Filter4);
	   	Filter5.Connect(Filter4); // This essentially says, "connect Filter5's input port to Filter3's output port
	   	Filter4.Connect(Filter3); // This essentially says, "connect Filter4's input port to Filter3's output port
		Filter3.Connect(Filter2); // This essentially says, "connect Filter3's input port to Filter2's output port
		Filter2.Connect(Filter1); // This essentially says, "connect Filter2's input port to Filter1's output port


		/****************************************************************************
		* Here we start the filters up.
		****************************************************************************/


		Filter1.start();
		Filter2.start();
		Filter3.start();
	   	Filter4.start();
	   	Filter5.start();
	    Filter6.start();
	   	//Filter7.start();


   } // main

} // Plumber