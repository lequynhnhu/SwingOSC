/**
 *	SwingOSC replacement for Pen
 *
 *	Implementation note: JSCWindow and JSCUserView
 *	call protRefresh when the draw func should be
 *	re-executed. In protRefresh, the draw func is
 *	executed, resulting in an array of short OSC
 *	sub commands, bracketed by special begin/end
 *	statements which allow the OSC commands to
 *	be spread over more than one bundle.
 *
 *	These get send to a java Pen object
 *	which maps them to Java2D operations. The Pen
 *	object implements the Icon interface and hence
 *	it can be added to a JLabel or the special Frame
 *	class for example.
 *
 *	Changelog
 *		03-Oct-06		separate font_ method, font and colour
 *					removed in string methods!!
 *		Jan-07		bundle size increased to 8K again
 *					; fixes missing List -> asSwingArg ; setSmoothing
 *
 *	@version		0.53, 02-Jul-07
 *	@author		Hanns Holger Rutz
 *
 *	@todo	check if String.bounds is cross platform or not
 *			(might have to replace)
 */
JPen {
	classvar cmds;
		
//	// JJJ begin
//	*initClass {
//		cmds = List.new;
//	}
//	// JJJ end

	*use { arg function;
		var res;
		this.push;
		res = function.value;
		this.pop;
		^res
	}

// ------------- affine transforms -------------

	*translate { arg x=0, y=0;
		cmds = cmds.add([ "trn", x, y ]);
	}

	*scale { arg x=0, y=0;
		cmds = cmds.add([ "scl", x, y ]);
	}

	*skew { arg x=0, y=0;
		cmds = cmds.add([ "shr", x, y ]);
	}

	*rotate { arg angle=0, x=0, y=0;
		cmds = cmds.add([ "rot", angle, x, y ]);
	}

	*matrix_ { arg array;
		cmds = cmds.add([ "mat" ] ++ array );
	}

// ------------- color and stroke customization (setter methods) -------------

	// JJJ begin
	*strokeColor_ { arg color;
		cmds = cmds.add([ "dco", color.red, color.green, color.blue, color.alpha ]);
	}
	// JJJ end

	// JJJ begin
	*fillColor_ { arg color;
		cmds = cmds.add([ "fco", color.red, color.green, color.blue, color.alpha ]);
	}
	// JJJ end
	
	*color_ { arg color;
		this.strokeColor_( color );
		this.fillColor_( color );
	}
	
	*width_ { arg width=1;
		cmds = cmds.add([ "stk", width ]);
	}

	// JJJ begin
	*font_ { arg font;
		cmds = cmds.add([ "fnt", font.name, font.size, font.style ]);
	}
	// JJJ end

// ------------- path composition -------------

	*path { arg function;
		var res;
		this.beginPath;
		res = function.value;
		this.endPath;
		^res
	}

	*beginPath {
		cmds = cmds.add([ "rst" ]);
	}

	*moveTo { arg point;
		cmds = cmds.add([ "mov", point.x, point.y ]);
	}

	*lineTo { arg point;
		cmds = cmds.add([ "lin", point.x, point.y ]);
	}

	*line { arg p1, p2;
		^this.moveTo( p1 ).lineTo( p2 );
	}

	*curveTo { arg point, cpoint1, cpoint2;
		cmds = cmds.add([ "cub", cpoint1.x, cpoint1.y, cpoint2.x, cpoint2.y, point.x, point.y ]);
	}

	*quadCurveTo { arg point, cpoint1;
		cmds = cmds.add([ "qua", cpoint1.x, cpoint1.y, point.x, point.y ]);
	}

	*addArc { arg center, radius, startAngle, arcAngle;
		cmds = cmds.add([ "arc", center.x, center.y, radius, startAngle, arcAngle ]);
	}

	*addWedge { arg center, radius, startAngle, arcAngle;
		cmds = cmds.add([ "pie", center.x, center.y, radius, startAngle, arcAngle ]);
	}

	*addAnnularWedge { arg center, innerRadius, outerRadius, startAngle, arcAngle;
		cmds = cmds.add([ "cyl", center.x, center.y, innerRadius, outerRadius,
					           startAngle, arcAngle ]);
	}

	*addRect { arg rect;
		cmds = cmds.add([ "rec", rect.left, rect.top, rect.width, rect.height ]);
	}

	*stroke {
		cmds = cmds.add([ "drw" ]);
	}

	*fill {
		cmds = cmds.add([ "fll" ]);
	}

	*clip {
		cmds = cmds.add([ "clp" ]);
	}
	
// ------------- direct drawing commands -------------

	*strokeRect { arg rect;
		cmds = cmds.add([ "drc", rect.left, rect.top, rect.width, rect.height ]);
	}

	*fillRect { arg rect;
		cmds = cmds.add([ "frc", rect.left, rect.top, rect.width, rect.height ]);
	}

	*strokeOval { arg rect;
		cmds = cmds.add([ "dov", rect.left, rect.top, rect.width, rect.height ]);
	}

	*fillOval { arg rect;
		cmds = cmds.add([ "fov", rect.left, rect.top, rect.width, rect.height ]);
	}
	
	*drawAquaButton { arg rect, type=0, down=false, on=false;
		// XXX
		(thisMethod.name ++ " not implemented").warn;
	}

	*setSmoothing { arg flag=true;
		cmds.add([ "ali", flag.binaryValue ]);
	}

	*string { arg str;
		this.stringAtPoint( str, Point( 0, 0 ));
	}
	
	*stringAtPoint { arg str, point;
		cmds = cmds.add([ "dst", str, point.x, point.y ]);
	}
	
	*stringInRect { arg str, rect;
		cmds = cmds.add([ "dsr", str, rect.left, rect.top, rect.width, rect.height, 0, 0 ]);
	}
	
	*stringCenteredIn { arg str, inRect;
		cmds = cmds.add([ "dsr", str, inRect.left, inRect.top, inRect.width, inRect.height, 0.5, 0.5 ]);
	}
	
	*stringLeftJustIn { arg str, inRect;
		cmds = cmds.add([ "dsr", str, inRect.left, inRect.top, inRect.width, inRect.height, 0, 0.5 ]);
	}
	
	*stringRightJustIn { arg str, inRect;
		cmds = cmds.add([ "dsr", str, inRect.left, inRect.top, inRect.width, inRect.height, 1, 0.5 ]);
	}

	
// ------------ from extPlot2D (swiki) ------------

	*addField { arg array, bounds, selector=\fillRect, colorFunc=Color.grey(_), legato=1.0;
		var rows, cols, width, height, y, l;
		if(array.rank != 2) { Error("array not a 2D matrix").throw };
		#rows, cols = array.shape;
		height = bounds.height;
		width = bounds.width;
		this.use {
			rows.do { |i|
				cols.do { |j|
					var y = array[i][j];
					this.color = colorFunc.(y);
					l = legato.(y);
					this.perform(selector,
						Rect(
							width / cols * j, 
							height / rows * i, 
							width / cols * l + 1, // "trapping"
							height / rows * l + 1
						)
					);
				}
			}
		};
	}

// ------------ private ------------

	//PRIVATE:
	*push {
		cmds = cmds.add([ "psh" ]);
	}

	*pop {
		cmds = cmds.add([ "pop" ]);
	}
	
	// called by JSCWindow, JSCUserView
	*protRefresh { arg func, view, server, penID, cmpID;
		var bndl, off, stop, len, numCmd, nextLen, maxBndlSize, floatSize;
	
// this is about 2% faster but won't deal with different draw funcs
//		cmds 	= Array( cmds.size.max( 8 ));
		cmds 	= nil;
		func.value( view );
		bndl		= List.new;
		bndl.add([ '/method', penID, \beginRec ]);
		off		= 0;
		len		= 92;	// [ #bundle, [ '/method', int, \beginRec ] (48)
						// + [ '/method', int, \add, '[', '/array', ']' ]] (44)
		stop		= off;
		numCmd	= cmds.size;
		maxBndlSize = server.options.oscBufSize - 60;	// 8132 = 8192 - 56 (see below) - 4 (max. boundary alignment)
		floatSize	= if( server.useDoubles, 8, 4 );

		while({ stop < numCmd }, {
//			nextLen	= cmds[ stop ].size * 5;
			nextLen	= cmds[ stop ].size;	// i.e. type tags section size
			// note: aNumber.size == 0, so will become 4! XXX beware if using NetAddr.useDoubles_( true ) !!
//			cmds[ stop ].do({ arg cmd; nextLen = nextLen + ((cmd.size + 4) & -4) });
			cmds[ stop ].do({ arg cmd; nextLen = nextLen + if( cmd.isFloat, floatSize, { (cmd.size + 4) & -4 })});
			if( len > maxBndlSize, {
// WARNING: COPYRANGE USES AN INCLUSIVE STOP INDEX!!!!
				bndl.add([ '/method', penID, \add ] ++ cmds.copyRange( off, stop - 1 ).flatten.asSwingArg );
//("FLUSHING LEN = "++len).postln;
				server.listSendBundle( nil, bndl );
				bndl = List.new;
				len	= 60;	// [ #bundle, [ '/method', int, \add, '[', '/array', ']' ]] (60)
				off	= stop;
			});
			len	= len + nextLen;
			stop = stop + 1;
		});
		if( off < stop, {
			bndl.add([ '/method', penID, \add ] ++ cmds.copyRange( off, stop - 1 ).flatten.asSwingArg );
		});
		// these are 56 additional bytes:
		bndl.add([ '/method', penID, \stopRec ]);
		if( cmpID.notNil, { bndl.add([ '/method', cmpID, \repaint ])});

//("FLUSHING FINAL LEN = "++(len+56)).postln;
		server.listSendBundle( nil, bndl );
		cmds 	= nil;
	}
}