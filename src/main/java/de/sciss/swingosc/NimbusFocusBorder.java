package de.sciss.swingosc;

import javax.swing.JComponent;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class NimbusFocusBorder implements Border {
    private static final RoundRectangle2D rect = new RoundRectangle2D.Float();
    private static final Area area = new Area();

    private static final NimbusFocusBorder rectInstance = new NimbusFocusBorder();

    public static NimbusFocusBorder getRectangle() { return rectInstance; }
    public static NimbusFocusBorder getRoundedRectangle( float radius ) { return new NimbusFocusBorder( radius );}

    private final float arcExtInner;
    private final float arcExtOuter;

    public NimbusFocusBorder() {
        arcExtInner = 0f;
        arcExtOuter = 0f;
    }

    public NimbusFocusBorder( float rounded ) {
        arcExtInner = rounded * 2;
        arcExtOuter = arcExtInner + 2.8f;
    }

    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        final Graphics2D g2 = (Graphics2D) g;
        area.reset();
        if( c.hasFocus() && focusVisible( c )) { paintBorderFocused( g2, x, y, width, height );}
    }

    private boolean focusVisible( Component c ) {
        if( c instanceof JComponent ) {
            final JComponent jc = (JComponent) c;
            final Boolean b = (Boolean) jc.getClientProperty( "swingosc.FocusVisible" );
            return b == null || b;
        } else return true;
    }

    public Insets getBorderInsets( Component c ) {
        return new Insets( 2, 2, 2, 2 );
    }

    public boolean isBorderOpaque() {
        return false;
    }

    private void paintBorderFocused( Graphics2D g, int x, int y, int width, int height ) {
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setColor( NimbusHelper.getFocusColor() );
//        rect.setRect( x + 0.6, y + 0.6, width - 1.2, height - 1.2 );
        rect.setRoundRect( x + 0.6, y + 0.6, width - 1.2, height - 1.2, arcExtOuter, arcExtOuter );
        area.add( new Area( rect ));
        rect.setRoundRect( x + 2, y + 2, width - 4, height - 4, arcExtInner, arcExtInner );
        area.subtract( new Area( rect ));
        g.fill( area );
    }
}