/*
 * -------------------------------------------------------------------------
 *	$Id: Complex.java,v 1.3 2006/02/13 17:40:03 pfisterer Exp $
 * -------------------------------------------------------------------------
 * Copyright (c) 1997 - 1998 by Visual Numerics, Inc. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted by Visual Numerics, Inc., provided that the copyright notice
 * above and the following warranty disclaimer are preserved in human
 * readable form.
 *
 * Because this software is licenses free of charge, it is provided
 * "AS IS", with NO WARRANTY.  TO THE EXTENT PERMITTED BY LAW, VNI
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO ITS PERFORMANCE, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * VNI WILL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE
 * OF OR INABILITY TO USE THIS SOFTWARE, INCLUDING BUT NOT LIMITED TO DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, PUNITIVE, AND EXEMPLARY DAMAGES, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
 *
 * -------------------------------------------------------------------------
 */


package org.tritonus.lowlevel.dsp;
 
 /**
  * This class implements complex numbers. It provides the basic operations
  * (addition, subtraction, multiplication, division) as well as a set of
  * complex functions.
  *
  * The binary operations have the form, where op is <code>plus</code>,
  *	<code>minus</code>, <code>times</code> or <code>over</code>.
  *	<pre>
  *	public static Complex op(Complex x, Complex y)   // x op y
  *	public static Complex op(Complex x, double y)    // x op y
  *	public static Complex op(double x, Complex y)    // x op y
  *	public Complex op(Complex y)                     // this op y
  *	public Complex op(double y)                      // this op y
  *	public Complex opReverse(double x)               // x op this
  * </pre>
  *
  *	The functions in this class follow the rules for complex  arithmetic
  * as defined C9x Annex G:"IEC 559-compatible complex arithmetic."
  * The API is not the same, but handling of infinities, NaNs, and positive
  * and negative zeros is intended to follow the same rules.
  *
  *	This class depends on the standard java.lang.Math class following
  * certain rules, as defined in the C9x Annex F, for the handling of
  * infinities, NaNs, and positive and negative zeros. Sun's specification
  *	is that java.lang.Math should reproduce the results in the Sun's fdlibm
  * C library. This library appears to follow the Annex F specification.
  *	At least on Windows, Sun's JDK 1.0 and 1.1 do NOT follow this specification.
  *	Sun's JDK 1.2(RC2) does follow the Annex F specification. Thesefore,
  * this class will not give the expected results for edge cases with
  *	JDK 1.0 and 1.1.
  */
public class Complex implements java.io.Serializable, Cloneable
{
	/**	
	 *	@serial Real part of the Complex.
	 */
	private double m_re;

	/**
	 *	@serial Imaginary part of the Complex.
	 */
	private double m_im;

	/**
	 *	Serialization ID
	 */
	static final long serialVersionUID = -633126172485117692L;


	/**
	 *  String used in converting Complex to String.
	 *  Default is "i", but sometimes "j" is desired.
	 *  Note that this is set for the class, not for
	 *  a particular instance of a Complex.
	 */
	public static String suffix = "i";
	

	private final static long negZeroBits =
		Double.doubleToLongBits(1.0/Double.NEGATIVE_INFINITY);



	/** 
	 *	Constructs a Complex equal to the argument.
	 *	@param	z	A Complex object
	 *			If z is null then a NullPointerException is thrown.
	 */
	public Complex(Complex z)
	{
		m_re = z.m_re;
		m_im = z.m_im;
	}


	/** 
	 *	Constructs a Complex with real and imaginary parts given
	 *	by the input arguments.
	 *	@param	re	A double value equal to the real part of the Complex object.
	 *	@param	im	A double value equal to the imaginary part of the Complex object.
	 */
	public Complex(double re, double im)
	{
		this.m_re = re;
		this.m_im = im;
	}


	/** 
	 *	Constructs a Complex with a zero imaginary part. 
	 *	@param	re	A double value equal to the real part of the Complex object.
	 */
	public Complex(double re)
	{
		this.m_re = re;
		this.m_im = 0.0;
	}


	/**
	 *	Constructs a Complex equal to zero.
	 */
	public Complex()
	{
		m_re = 0.0;
		m_im = 0.0;
	}
	

	/** 
	 *	Tests if this is a complex Not-a-Number (NaN) value. 
	 *	@return  True if either component of the Complex object is NaN;
	 *	false, otherwise. 
	 */
	private boolean isNaN()
	{
		return (Double.isNaN(m_re) || Double.isNaN(m_im));
	}

	
	/** 
	 *	Compares with another Complex. 
     *	<p><em>Note: To be useful in hashtables this method
     *	considers two NaN double values to be equal. This
     *	is not according to IEEE specification.</em>
	 *	@param	z	A Complex object.
	 *	@return True if the real and imaginary parts of this object
	 *			are equal to their counterparts in the argument; false, otherwise.
	 */
	public boolean equals(Complex z)
	{
		if (isNaN() && z.isNaN()) {
			return true;
		} else {
			return (m_re == z.m_re  &&  m_im == z.m_im);
		}
	}

   
	/**
     *	Compares this object against the specified object.
     *	<p><em>Note: To be useful in hashtables this method
     *	considers two NaN double values to be equal. This
     *	is not according to IEEE specification</em>
     *	@param	obj	The object to compare with.
     *	@return True if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		} else if (obj instanceof Complex) {
			return equals((Complex)obj);
		} else {
			return false;
		}
	}

    /**
     *	Returns a hashcode for this Complex.
     *	@return  A hash code value for this object. 
     */
    public int hashCode()
	{
		long re_bits = Double.doubleToLongBits(m_re);
		long im_bits = Double.doubleToLongBits(m_im);
		return (int)((re_bits^im_bits)^((re_bits^im_bits)>>32));
    }

	/** 
	 *	Returns the real part of a Complex object. 
	 *	@return	The real part of z.
	 */
	public double real()
	{
		return m_re;
	}


	/** 
	 *	Returns the imaginary part of a Complex object. 
	 *	@param	z	A Complex object.
	 *	@return	The imaginary part of z.
	 */
	public double imag()
	{
		return m_im;
	}
	
	
	/** 
	 *	Returns the real part of a Complex object. 
	 *	@param	z	A Complex object.
	 *	@return	The real part of z.
	 */
	public static double real(Complex z)
	{
		return z.m_re;
	}


	/** 
	 *	Returns the imaginary part of a Complex object. 
	 *	@param	z	A Complex object.
	 *	@return	The imaginary part of z.
	 */
	public static double imag(Complex z)
	{
		return z.m_im;
	}


	/** 
	 *	Returns the negative of a Complex object, -z. 
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to
	 *			the negative of the argument.
	 */
	public static Complex negative(Complex z)
	{
		return new Complex(-z.m_re, -z.m_im);
	}

	
	/** 
	 *	Returns the complex conjugate of a Complex object.
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to complex conjugate of z.
	 */
	public static Complex conjugate(Complex z)
	{
		return new Complex(z.m_re, -z.m_im);
	}

	
	/** 
	 *	Returns the sum of two Complex objects, x+y.
	 *	@param	x	A Complex object.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x+y.
	 */
	public static Complex plus(Complex x, Complex y)
	{
		return new Complex(x.m_re+y.m_re, x.m_im+y.m_im);
	}

	/** 
	 *	Returns the sum of a Complex and a double, x+y. 
	 *	@param	x	A Complex object.
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to x+y.
	 */
	public static Complex plus(Complex x, double y)
	{
		return new Complex(x.m_re+y, x.m_im);
	}

	/** 
	 *	Returns the sum of a double and a Complex, x+y. 
	 *	@param	x	A double value.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x+y.
	 */
	public static Complex plus(double x, Complex y)
	{
		return new Complex(x+y.m_re, y.m_im);
	}

	/** 
	 *	Returns the sum of this Complex and another Complex, this+y. 
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to this+y.
	 */
	public Complex plus(Complex y)
	{
		return new Complex(m_re+y.m_re, m_im+y.m_im);
	}

	/** 
	 *	Returns the sum of this Complex a double, this+y. 
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to this+y.
	 */
	public Complex plus(double y)
	{
		return new Complex(m_re+y, m_im);
	}
	
	/** 
	 *	Returns the sum of this Complex and a double, x+this. 
	 *	@param	x	A double value.
	 *	@return A newly constructed Complex initialized to x+this.
	 */
	public Complex plusReverse(double x)
	{
		return new Complex(m_re+x, m_im);
	}


	/** 
	 *	Returns the difference of two Complex objects, x-y.
	 *	@param	x	A Complex object.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x-y.
	 */
	public static Complex minus(Complex x, Complex y)
	{
		return new Complex(x.m_re-y.m_re, x.m_im-y.m_im);
	}

	/** 
	 *	Returns the difference of a Complex object and a double, x-y. 
	 *	@param	x	A Complex object.
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to x-y.
	 */
	public static Complex minus(Complex x, double y)
	{
		return new Complex(x.m_re-y, x.m_im);
	}
	
	/** 
	 *	Returns the difference of a double and a Complex object, x-y. 
	 *	@param	x	A double value.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x-y..
	 */
	public static Complex minus(double x, Complex y)
	{
		return new Complex(x-y.m_re, -y.m_im);
	}

	/** 
	 *	Returns the difference of this Complex object and
	 *	another Complex object, this-y. 
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to this-y.
	 */
	public Complex minus(Complex y)
	{
		return new Complex(m_re-y.m_re, m_im-y.m_im);
	}

	/** 
	 *	Subtracts a double from this Complex and returns the difference, this-y.
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to this-y.
	 */
	public Complex minus(double y)
	{
		return new Complex(m_re-y, m_im);
	}


	/** 
	 *	Returns the difference of this Complex object and a double, this-y.
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to x-this.
	 */
	public Complex minusReverse(double x)
	{
		return new Complex(x-m_re, -m_im);
	}


	/** 
	 *	Returns the product of two Complex objects, x*y. 
	 *	@param	x	A Complex object.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x*y.
	 */
	public static Complex times(Complex x, Complex y)
	{
		Complex t = new Complex(x.m_re*y.m_re-x.m_im*y.m_im, x.m_re*y.m_im+x.m_im*y.m_re);
		if (Double.isNaN(t.m_re) && Double.isNaN(t.m_im))
			timesNaN(x, y, t);
		return t;
	}

	/*
	 *	Returns sign(b)*|a|.
	 */
	private static double copysign(double a, double b)
	{
		double abs = Math.abs(a);
		return ((b < 0) ? -abs : abs);
	}

	/**
	 *	Recovers infinities when computed x*y = NaN+i*NaN.
	 *	This code is not part of times(), so that times
	 *	could be inlined by an optimizing compiler.
	 *	<p>
	 *	This algorithm is adapted from the C9x Annex G:
	 *	"IEC 559-compatible complex arithmetic."
	 *	@param	x	First Complex operand.
	 *	@param	y	Second Complex operand.
	 *	@param	t	The product x*y, computed without regard to NaN.
	 *				The real and/or the imaginary part of t is
	 *				expected to be NaN.
	 *	@return	The corrected product of x*y.
	 */
	private static void timesNaN(Complex x, Complex y, Complex t)
	{
		boolean	recalc = false;
		double	a = x.m_re;
		double	b = x.m_im;
		double	c = y.m_re;
		double	d = y.m_im;

		if (Double.isInfinite(a) || Double.isInfinite(b)) {
			// x is infinite
			a = copysign(Double.isInfinite(a)?1.0:0.0, a);
			b = copysign(Double.isInfinite(b)?1.0:0.0, b);
			if (Double.isNaN(c))  c = copysign(0.0, c);
			if (Double.isNaN(d))  d = copysign(0.0, d);
			recalc = true;
		}

		if (Double.isInfinite(c) || Double.isInfinite(d)) {
			// x is infinite
			a = copysign(Double.isInfinite(c)?1.0:0.0, c);
			b = copysign(Double.isInfinite(d)?1.0:0.0, d);
			if (Double.isNaN(a))  a = copysign(0.0, a);
			if (Double.isNaN(b))  b = copysign(0.0, b);
			recalc = true;
		}

		if (!recalc) {
			if (Double.isInfinite(a*c) || Double.isInfinite(b*d) ||
				Double.isInfinite(a*d) || Double.isInfinite(b*c)) {
				// Change all NaNs to 0
				if (Double.isNaN(a))  a = copysign(0.0, a);
				if (Double.isNaN(b))  b = copysign(0.0, b);
				if (Double.isNaN(c))  c = copysign(0.0, c);
				if (Double.isNaN(d))  d = copysign(0.0, d);
				recalc = true;
			}
		}

		if (recalc) {
			t.m_re = Double.POSITIVE_INFINITY * (a*c - b*d);
			t.m_im = Double.POSITIVE_INFINITY * (a*d + b*c);
		}
	}


	/** 
	 *	Returns the product of a Complex object and a double, x*y. 
	 *	@param	x	A Complex object.
	 *	@param	y	A double value.
	 *	@return  A newly constructed Complex initialized to x*y.
	 */
	public static Complex times(Complex x, double y)
	{
		return new Complex(x.m_re*y, x.m_im*y);
	}

	/** 
	 *	Returns the product of a double and a Complex object, x*y. 
	 *	@param	x	A double value.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized to x*y.
	 */
	public static Complex times(double x, Complex y)
	{
		return new Complex(x*y.m_re, x*y.m_im);
	}

	/** 
	 * Returns the product of this Complex object and another Complex object, this*y. 
	 * @param	y	A Complex object.
	 * @return  A newly constructed Complex initialized to this*y.
	 */
	public Complex times(Complex y)
	{
		return times(this,y);
	}

	/** 
	 *	Returns the product of this Complex object and a double, this*y.
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to this*y.
	 */
	public Complex times(double y)
	{
		return new Complex(m_re*y, m_im*y);
	}

	/** 
	 *	Returns the product of a double and this Complex, x*this. 
	 *	@param	y	A double value.
	 *	@return A newly constructed Complex initialized to x*this.
	 */
	public Complex timesReverse(double x)
	{
		return new Complex(x*m_re, x*m_im);
	}


	private static boolean isFinite(double x)
	{
		return !(Double.isInfinite(x) || Double.isNaN(x));
	}


	/** 
	 *	Returns Complex object divided by a Complex object, x/y. 
	 *	@param	x	The numerator, a Complex object.
	 *	@param	y	The denominator, a Complex object.
	 *	@return A newly constructed Complex initialized to x/y.
	 */
	public static Complex over(Complex x, Complex y)
	{
		double	a = x.m_re;
		double	b = x.m_im;
		double	c = y.m_re;
		double	d = y.m_im;

		double scale = Math.max(Math.abs(c), Math.abs(d));
		boolean isScaleFinite = isFinite(scale);
		if (isScaleFinite) {
			c /= scale;
			d /= scale;
		}

		double den = c*c + d*d;
		Complex z = new Complex((a*c+b*d)/den, (b*c-a*d)/den);
		
		if (isScaleFinite) {
			z.m_re /= scale;
			z.m_im /= scale;
		}

		// Recover infinities and zeros computed as NaN+iNaN.
		if (Double.isNaN(z.m_re) && Double.isNaN(z.m_im)) {
			if (den == 0.0  && (!Double.isNaN(a) || !Double.isNaN(b))) {
				double s = copysign(Double.POSITIVE_INFINITY, c);
				z.m_re = s * a;
				z.m_im = s * b;
			
			} else if ((Double.isInfinite(a) || Double.isInfinite(b)) &&
				isFinite(c) && isFinite(d)) {
				a = copysign(Double.isInfinite(a)?1.0:0.0, a);
				b = copysign(Double.isInfinite(b)?1.0:0.0, b);
				z.m_re = Double.POSITIVE_INFINITY * (a*c + b*d);
				z.m_im = Double.POSITIVE_INFINITY * (b*c - a*d);
			
			} else if (Double.isInfinite(scale)  &&
				isFinite(a) && isFinite(b)) {
				c = copysign(Double.isInfinite(c)?1.0:0.0, c);
				d = copysign(Double.isInfinite(d)?1.0:0.0, d);
				z.m_re = 0.0 * (a*c + b*d);
				z.m_im = 0.0 * (b*c - a*d);
			}
		}
		return z;
	}

	
	/** 
	 *	Returns Complex object divided by a double, x/y.
	 *	@param	x	The numerator, a Complex object.
	 *	@param	y	The denominator, a double.
	 *	@return A newly constructed Complex initialized to x/y.
	 */
	public static Complex over(Complex x, double y)
	{
		return new Complex(x.m_re/y, x.m_im/y);
	}

	/** 
	 *	Returns a double divided by a Complex object, x/y. 
	 *	@param	x	A double value.
	 *	@param	y	The denominator, a Complex object.
	 *	@return A newly constructed Complex initialized to x/y.
	 */
	public static Complex over(double x, Complex y)
	{
		return y.overReverse(x);
	}

	/** 
	 *	Returns this Complex object divided by another Complex object, this/y. 
	 *	@param	y	The denominator, a Complex object.
	 *	@return A newly constructed Complex initialized to x/y.
	 */
	public Complex over(Complex y)
	{
		return over(this, y);
	}

	/** 
	 *	Returns this Complex object divided by double, this/y. 
	 *	@param	y	The denominator, a double.
	 *	@return  A newly constructed Complex initialized to x/y.
	 */
	public Complex over(double y)
	{
		return over(this, y);
	}

	/** 
	 *	Returns a double dividied by this Complex object, x/this. 
	 *	@param	x	The numerator, a double.
	 *	@return A newly constructed Complex initialized to x/this.
	 */
	public Complex overReverse(double x)
	{
        double	den, t;
		Complex z;
        if (Math.abs(m_re) > Math.abs(m_im)) {
            t = m_im / m_re;
            den = m_re + m_im*t;
            z = new Complex(x/den, -x*t/den);
        } else {
            t = m_re / m_im;
            den = m_im + m_re*t;
            z = new Complex(x*t/den, -x/den);
        }
        return z;
	}



	/** 
	 *	Returns the absolute value (modulus) of a Complex, |z|. 
	 *	@param	z	A Complex object.
	 *	@return A double value equal to the absolute value of the argument.
	 */
	public static double abs(Complex z)
	{
		double x = Math.abs(z.m_re);
		double y = Math.abs(z.m_im);
		
		if (Double.isInfinite(x) || Double.isInfinite(y))
			return Double.POSITIVE_INFINITY;
		
		if (x + y == 0.0) {
			return 0.0;
		} else if (x > y) {
			y /= x;
			return x*Math.sqrt(1.0+y*y);
		} else {
			x /= y;
			return y*Math.sqrt(x*x+1.0);
		}
	}


	/** 
	 *	Returns the argument (phase) of a Complex, in radians,
	 *	with a branch cut along the negative real axis.
	 *	@param	z	A Complex object.
	 *	@return A double value equal to the argument (or phase) of a Complex.
	 *			It is in the interval [-pi,pi].
	 */
	public static double argument(Complex z)
	{
		return Math.atan2(z.m_im, z.m_re);
	}

	
	/** 
	 *	Returns the square root of a Complex,
	 *	with a branch cut along the negative real axis.
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized
	 *			to square root of z. Its real part is
	 *			non-negative.
	 */
	public static Complex sqrt(Complex z)
	{
		Complex	result = new Complex();

		if (Double.isInfinite(z.m_im)) {
			result.m_re = Double.POSITIVE_INFINITY;
			result.m_im = z.m_im;
		} else if (Double.isNaN(z.m_re)) {
			result.m_re = result.m_im = Double.NaN;
		} else if (Double.isNaN(z.m_im)) {
			if (Double.isInfinite(z.m_re)) {
				if (z.m_re > 0) {
					result.m_re = z.m_re;
					result.m_im = z.m_im;
				} else {
					result.m_re = z.m_im;
					result.m_im = Double.POSITIVE_INFINITY;
				}
			} else {
				result.m_re = result.m_im = Double.NaN;
			}
		} else {
			// Numerically correct version of formula 3.7.27
			// in the NBS Hanbook, as suggested by Pete Stewart.
			double t = abs(z);
		
			if (Math.abs(z.m_re) <= Math.abs(z.m_im)) {
				// No cancellation in these formulas
				result.m_re = Math.sqrt(0.5*(t+z.m_re));
				result.m_im = Math.sqrt(0.5*(t-z.m_re));
			} else {
				// Stable computation of the above formulas
				if (z.m_re > 0) {
					result.m_re = t + z.m_re;
					result.m_im = Math.abs(z.m_im)*Math.sqrt(0.5/result.m_re);
					result.m_re = Math.sqrt(0.5*result.m_re);
				} else {
					result.m_im = t - z.m_re;
					result.m_re = Math.abs(z.m_im)*Math.sqrt(0.5/result.m_im);
					result.m_im = Math.sqrt(0.5*result.m_im);
				}
			}
			if (z.m_im < 0)
				result.m_im = -result.m_im;
		}
		return result;
	}


	/** 
	 *	Returns the exponential of a Complex z, exp(z).
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to exponential
	 *			of the argument. 
	 */
	public static Complex exp(Complex z)
	{
		Complex result = new Complex();
		
		double r = Math.exp(z.m_re);

		double cosa = Math.cos(z.m_im);
		double sina = Math.sin(z.m_im);
		if (Double.isInfinite(z.m_im) || Double.isNaN(z.m_im) || Math.abs(cosa)>1) {
			cosa = sina = Double.NaN;
		
		}

		if (Double.isInfinite(z.m_re) || Double.isInfinite(r)) {
			if (z.m_re < 0) {
				r = 0;
				if (Double.isInfinite(z.m_im)  ||  Double.isNaN(z.m_im)) {
					cosa = sina = 0;
				} else {
					cosa /= Double.POSITIVE_INFINITY;
					sina /= Double.POSITIVE_INFINITY;
				}
			} else {
				r = z.m_re;
				if (Double.isNaN(z.m_im)) cosa = 1;
			}
		}
        
		if (z.m_im == 0.0) {
			result.m_re = r;
			result.m_im = z.m_im;
		} else {
			result.m_re = r*cosa;
			result.m_im = r*sina;
		}
		return result;
	}


	/** 
	 *	Returns the logarithm of a Complex z,
	 *	with a branch cut along the negative real axis.
	 *	@param	z	A Complex object.
	 *	@return  A newly constructed Complex initialized to logarithm
	 *			of the argument. Its imaginary part is in the
	 *			interval [-i*pi,i*pi].
	 */
	public static Complex log(Complex z)
	{
		Complex	result = new Complex();

		if (Double.isNaN(z.m_re)) {
			result.m_re = result.m_im = z.m_re;
			if (Double.isInfinite(z.m_im))
				result.m_re = Double.POSITIVE_INFINITY;
		} else if (Double.isNaN(z.m_im)) {
			result.m_re = result.m_im = z.m_im;
			if (Double.isInfinite(z.m_re))
				result.m_re = Double.POSITIVE_INFINITY;
		} else {
			result.m_re = Math.log(abs(z));
			result.m_im = argument(z);
		}
		return result;
	}


	/** 
	 *	Returns the sine of a Complex. 
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to sine of the argument.
	 */
	public static Complex sin(Complex z)
	{
		// sin(z) = -i*sinh(i*z)
		Complex iz = new Complex(-z.m_im,z.m_re);
		Complex s = sinh(iz);
		double re = s.m_im;
		s.m_im = -s.m_re;
		s.m_re = re;
		return s;
	}


	/** 
	 *	Returns the cosine of a Complex. 
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to cosine of the argument.
	 */
	public static Complex cos(Complex z)
	{
		// cos(z) = cosh(i*z)
		return cosh(new Complex(-z.m_im,z.m_re));
	}

	/** 
	 *	Returns the tangent of a Complex. 
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized
	 *			to tangent of the argument.
	 */
	public static Complex tan(Complex z)
	{
		// tan = -i*tanh(i*z)
		Complex iz = new Complex(-z.m_im,z.m_re);
		Complex s = tanh(iz);
		double re = s.m_im;
		s.m_im = -s.m_re;
		s.m_re = re;
		return s;
	}

	/** 
	 *	Returns the inverse sine (arc sine) of a Complex,
	 *	with branch cuts outside the interval [-1,1] along the
	 *	real axis.
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to inverse
	 *			(arc) sine of the argument. The real part of the
	 *			result is in the interval [-pi/2,+pi/2].
	 */
	public static Complex asin(Complex z)
	{
	    Complex	result = new Complex();

		double r = abs(z);

		if (Double.isInfinite(r)) {
			boolean infiniteX = Double.isInfinite(z.m_re);
			boolean infiniteY = Double.isInfinite(z.m_im);
			if (infiniteX) {
				double  pi2 = 0.5*Math.PI;
				result.m_re = (z.m_re>0 ? pi2 : -pi2);
				if (infiniteY) result.m_re /= 2;
			} else if (infiniteY) {
				result.m_re = z.m_re/Double.POSITIVE_INFINITY;
			}
			if (Double.isNaN(z.m_im)) {
				result.m_im = -z.m_re;
				result.m_re = z.m_im;
			} else {
				result.m_im = z.m_im*Double.POSITIVE_INFINITY;
			}
			return result;
		} else if (Double.isNaN(r)) {
			result.m_re = result.m_im = Double.NaN;
			if (z.m_re == 0)  result.m_re = z.m_re;
		} else if (r < 2.58095e-08) {
			// sqrt(6.0*dmach(3)) = 2.58095e-08
			result.m_re = z.m_re;
			result.m_im = z.m_im;
		} else if (z.m_re == 0) {
			result.m_re = 0;
			result.m_im = Sfun.asinh(z.m_im);
		} else if (r <= 0.1) {
			Complex z2 = times(z,z);
			//log(eps)/log(rmax) = 8 where rmax = 0.1
			for (int i = 1;  i <= 8;  i++) {
				double twoi = 2*(8-i) + 1;
				result = times(times(result,z2),twoi/(twoi+1.0));
				result.m_re += 1.0/twoi;
			}
			result = result.times(z);
		} else {
			// A&S 4.4.26
			// asin(z) = -i*log(z+sqrt(1-z)*sqrt(1+z))
			// or, since log(iz) = log(z) +i*pi/2,
			// asin(z) = pi/2 - i*log(z+sqrt(z+1)*sqrt(z-1))
			Complex w = ((z.m_im < 0) ? negative(z) : z);
			Complex sqzp1 = sqrt(plus(w,1.0));
			if (sqzp1.m_im < 0.0)
				sqzp1 = negative(sqzp1);
			Complex sqzm1 = sqrt(minus(w,1.0));
			result = log(plus(w,times(sqzp1,sqzm1)));

			double rx = result.m_re;
			result.m_re = 0.5*Math.PI + result.m_im;
			result.m_im = -rx;
		}

		if (result.m_re > 0.5*Math.PI) {
			result.m_re = Math.PI - result.m_re;
			result.m_im = -result.m_im;
		}
		if (result.m_re < -0.5*Math.PI) {
			result.m_re = -Math.PI - result.m_re;
			result.m_im = -result.m_im;
		}
		if (z.m_im < 0) {
			result.m_re = -result.m_re;
			result.m_im = -result.m_im;
		}
		return result;
	}


	/** 
	 *	Returns the inverse cosine (arc cosine) of a Complex,
	 *	with branch cuts outside the interval [-1,1] along the
	 *	real axis.
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to
	 *			inverse (arc) cosine of the argument.
	 *			The real part of the result is in the interval [0,pi].
	 */
	public static Complex acos(Complex z)
	{
		Complex	result = new Complex();
		double r = abs(z);

		if (Double.isInfinite(z.m_re) && Double.isNaN(z.m_im)) {
			result.m_re = Double.NaN;
			result.m_im = Double.NEGATIVE_INFINITY;
		} else if (Double.isInfinite(r)) {
			result.m_re = Math.atan2(Math.abs(z.m_im),z.m_re);
			result.m_im = z.m_im*Double.NEGATIVE_INFINITY;
		} else if (r == 0) {
			result.m_re = Math.PI/2;
			result.m_im = -z.m_im;
		} else {
			result = minus(Math.PI/2,asin(z));
		}
		return result;
	}

	/** 
	 * Returns the inverse tangent (arc tangent) of a Complex,
	 * with branch cuts outside the interval [-i,i] along the
	 * imaginary axis.
	 * @param	z	A Complex object.
	 * @return  A newly constructed Complex initialized to
	 *			inverse (arc) tangent of the argument.
	 *			Its real part is in the interval [-pi/2,pi/2].
	 */
	public static Complex atan(Complex z)
	{
		Complex	result = new Complex();
		double	r = abs(z);

		if (Double.isInfinite(r)) {
			double  pi2 = 0.5*Math.PI;
			double im = (Double.isNaN(z.m_im) ? 0 : z.m_im);
			result.m_re = (z.m_re<0 ? -pi2 : pi2);
			result.m_im = (im<0 ? -1 : 1)/Double.POSITIVE_INFINITY;
			if (Double.isNaN(z.m_re))  result.m_re = z.m_re;
		} else if (Double.isNaN(r)) {
			result.m_re = result.m_im = Double.NaN;
			if (z.m_im == 0)  result.m_im = z.m_im;
		} else if (r < 1.82501e-08) {
			// sqrt(3.0*dmach(3)) = 1.82501e-08
			result.m_re = z.m_re;
			result.m_im = z.m_im;
		} else if (r < 0.1) {
			Complex z2 = times(z,z);
			// -0.4343*log(dmach(3))+1 = 17
			for (int k = 0;  k < 17;  k++) {
				Complex temp = times(z2,result);
				int twoi = 2*(17-k) - 1;
				result.m_re = 1.0/twoi - temp.m_re;
				result.m_im = -temp.m_im;
			}
			result = result.times(z);
		} else if (r < 9.0072e+15) {
			// 1.0/dmach(3) = 9.0072e+15
			double r2 = r*r;
			result.m_re = 0.5*Math.atan2(2*z.m_re,1.0-r2);
			result.m_im = 0.25*Math.log((r2+2*z.m_im+1)/(r2-2*z.m_im+1));
		} else {
			result.m_re = ((z.m_re < 0.0) ? -0.5*Math.PI : 0.5*Math.PI);
		}
		return result;
	}

	/** 
	 * Returns the hyperbolic sine of a Complex. 
	 * @param	z	A Complex object.
	 * @return  A newly constructed Complex initialized to hyperbolic
	 *			sine of the argument.
	 */
	public static Complex sinh(Complex z)
	{
		double	coshx = Sfun.cosh(z.m_re);
		double	sinhx = Sfun.sinh(z.m_re);
		double	cosy  = Math.cos(z.m_im);
		double	siny  = Math.sin(z.m_im);
		boolean infiniteX = Double.isInfinite(coshx);
		boolean infiniteY = Double.isInfinite(z.m_im);
		Complex result;

		if (z.m_im == 0) {
			result = new Complex(Sfun.sinh(z.m_re));
		} else {
			// A&S 4.5.49
			result = new Complex(sinhx*cosy, coshx*siny);
			if (infiniteY) {
				result.m_im = Double.NaN;
				if (z.m_re == 0)  result.m_re = 0;
			}
			if (infiniteX) {
				result.m_re = z.m_re*cosy;
				result.m_im = z.m_re*siny;
				if (z.m_im == 0)  result.m_im = 0;
				if (infiniteY) result.m_re = z.m_im;
			}
		}
		return result;
	}

	/** 
	 * Returns the hyperbolic cosh of a Complex. 
	 * @param	z	A Complex object.
	 * @return  A newly constructed Complex initialized to
	 *			the hyperbolic cosine of the argument.
	 */
	public static Complex cosh(Complex z)
	{
		if (z.m_im == 0) {
			return new Complex(Sfun.cosh(z.m_re));
		}
		
		double	coshx = Sfun.cosh(z.m_re);
		double	sinhx = Sfun.sinh(z.m_re);
		double	cosy  = Math.cos(z.m_im);
		double	siny  = Math.sin(z.m_im);
		boolean infiniteX = Double.isInfinite(coshx);
		boolean infiniteY = Double.isInfinite(z.m_im);

		// A&S 4.5.50
		Complex result = new Complex(coshx*cosy, sinhx*siny);
		if (infiniteY) 	result.m_re = Double.NaN;
		if (z.m_re == 0) {
			result.m_im = 0;
		} else if (infiniteX) {
			result.m_re = z.m_re*cosy;
			result.m_im = z.m_re*siny;
			if (z.m_im == 0)  result.m_im = 0;
			if (Double.isNaN(z.m_im)) {
				result.m_re = z.m_re;
			} else if (infiniteY) {
				result.m_re = z.m_im;
			}
		}
		return result;
	}

	/** 
	 * Returns the hyperbolic tanh of a Complex. 
	 * @param	z	A Complex object.
	 * @return  A newly constructed Complex initialized to
	 *			the hyperbolic tangent of the argument.
	 */
	public static Complex tanh(Complex z)
	{
		double	sinh2x = Sfun.sinh(2*z.m_re);
		
		if (z.m_im == 0) {
			return new Complex(Sfun.tanh(z.m_re));
		} else if (sinh2x == 0) {
			return new Complex(0,Math.tan(z.m_im));
		}

		double	cosh2x = Sfun.cosh(2*z.m_re);
		double	cos2y  = Math.cos(2*z.m_im);
		double	sin2y  = Math.sin(2*z.m_im);
		boolean infiniteX = Double.isInfinite(cosh2x);

		// Workaround for bug in JDK 1.2beta4
		if (Double.isInfinite(z.m_im) || Double.isNaN(z.m_im)) {
			cos2y = sin2y = Double.NaN;  
		}

		if (infiniteX)
			return new Complex(z.m_re > 0 ? 1 : -1);

		// A&S 4.5.51
		double den = (cosh2x + cos2y);
		return new Complex(sinh2x/den, sin2y/den);
	}
	
	/** 
	 *	Returns the Complex z raised to the x power,
	 *	with a branch cut for the first parameter (z) along the
	 *	negative real axis.
	 *	@param	z	A Complex object.
	 *	@param	x	A double value.
	 *	@return	A newly constructed Complex initialized to z to the power x.
	 */
	public static Complex pow(Complex z, double x)
	{
		double	absz = abs(z);
		Complex result = new Complex();
		
		if (absz == 0.0) {
			result = z;
		} else {
			double a = argument(z);
			double e = Math.pow(absz, x);
			result.m_re = e*Math.cos(x*a);
			result.m_im = e*Math.sin(x*a);
		}
		return result;
	}

	/** 
	 *	Returns the inverse hyperbolic sine (arc sinh) of a Complex,
	 *	with a branch cuts outside the interval [-i,i].
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to
	 *			inverse (arc) hyperbolic sine of the argument.
	 *			Its imaginary part is in the interval [-i*pi/2,i*pi/2].
	 */
	public static Complex asinh(Complex z)
	{
		// asinh(z) = i*asin(-i*z)
		Complex miz = new Complex(z.m_im,-z.m_re); 
		Complex result = asin(miz);
		double rx = result.m_im;
		result.m_im = result.m_re;
		result.m_re = -rx;
		return result;
	}
	
	/** 
	 *	Returns the inverse hyperbolic cosine (arc cosh) of a Complex,
	 *	with a branch cut at values less than one along the real axis.
	 *	@param	z	A Complex object.
	 *	@return A newly constructed Complex initialized to
	 *			inverse (arc) hyperbolic cosine of the argument.
	 *			The real part of the result is non-negative and its
	 *			imaginary part is in the interval [-i*pi,i*pi].
	 */
	public static Complex acosh(Complex z)
	{
		Complex result = acos(z);
		double rx = -result.m_im;
		result.m_im = result.m_re;
		result.m_re = rx;
		if (result.m_re < 0 || isNegZero(result.m_re)) {
			result.m_re = -result.m_re;
			result.m_im = -result.m_im;		
		}
		return result;
	}


	/**
	 *	Returns true is x is a negative zero.
	 */
	private static boolean isNegZero(double x)
	{
		return (Double.doubleToLongBits(x) == negZeroBits);
	}

	/** 
	 *	Returns the inverse hyperbolic tangent (arc tanh) of a Complex,
	 *	with a branch cuts outside the interval [-1,1] on the real axis.
	 *	@param	z	A Complex object.
	 *	@return	A newly constructed Complex initialized to
	 *			inverse (arc) hyperbolic tangent of the argument.
	 *			The imaginary part of the result is in the interval
	 *			[-i*pi/2,i*pi/2].
	 */
	public static Complex atanh(Complex z)
	{
		// atanh(z) = i*atan(-i*z)
		Complex miz = new Complex(z.m_im,-z.m_re); 
		Complex result = atan(miz);
		double rx = result.m_im;
		result.m_im = result.m_re;
		result.m_re = -rx;
		return result;

	}
	

	/** 
	 *	Returns the Complex x raised to the Complex y power. 
	 *	@param	x	A Complex object.
	 *	@param	y	A Complex object.
	 *	@return A newly constructed Complex initialized
	 *			to x<SUP><FONT SIZE="1">y</FONT></SUP><FONT SIZE="3">.
	 */
	public static Complex pow(Complex x, Complex y)
	{
		return exp(times(y,log(x)));
	}




	/** 
	 *	Returns a String representation for the specified Complex. 
	 *	@return A String representation for this object.
	 */
	public String toString()
	{
		if (m_im == 0.0)
			return String.valueOf(m_re);

		if (m_re == 0.0)
			return String.valueOf(m_im) + suffix;

		String sign = (m_im < 0.0) ? "" : "+";
		return (String.valueOf(m_re) + sign + String.valueOf(m_im) + suffix);
	}


	/** 
	 *	Parses a string into a Complex.
	 *	@param	s	The string to be parsed.
	 *	@return A newly constructed Complex initialized to the value represented 
	 *			by the string argument.
	 *	@exception NumberFormatException	If the string does not contain a parsable Complex number.
	 *  @exception NullPointerException		If the input argument is null.
	 */
	public static Complex valueOf(String s) throws NumberFormatException
	{
		String	input = s.trim();
		int		iBeginNumber = 0;
		Complex z = new Complex();
		int		state = 0;
		int		sign = 1;
		boolean	haveRealPart = false;

		/*
		 * state values
		 *	0	Initial State
		 *	1	After Initial Sign
		 *	2	In integer part
		 *	3	In fractional part
		 *	4	In exponential part (after 'e' but fore sign or digits)
		 *	5	In exponential digits
		 */
		for (int k = 0;  k < input.length();  k++) {
			
			char ch = input.charAt(k);

			switch (ch) {

			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				if (state == 0  ||	state == 1) {
					state = 2;
				} else if (state == 4) {
					state = 5;
				}
				break;

			case '-':
			case '+':
				sign = ((ch=='+') ? 1 : -1);
				if (state == 0) {
					state = 1;
				} else if (state == 4) {
					state = 5;
				} else {
					if (!haveRealPart) {
						// have the real part of the number
						z.m_re = Double.valueOf(input.substring(iBeginNumber,k)).doubleValue();
						haveRealPart = true;
						// perpare to part the imaginary part
						iBeginNumber = k;
						state = 1;
					} else {
						throw new NumberFormatException(input);
					}
				}
				break;

			case '.':
				if (state == 0  ||	state == 1  ||  state == 2)
					state = 3;
				else
					throw new NumberFormatException(input);
				break;
   
			case 'i': case 'I':
			case 'j': case 'J':
				if (k+1 != input.length()) {
					throw new NumberFormatException(input);
				} else if (state == 0  ||  state == 1) {
					z.m_im = sign;
					return z;
				} else if (state == 2  ||  state == 3  ||  state == 5) {
					z.m_im = Double.valueOf(input.substring(iBeginNumber,k)).doubleValue();
					return z;
				} else {
					throw new NumberFormatException(input);
				}
    			

    		case 'e': case 'E': case 'd': case 'D':
				if (state == 2  ||	state == 3) {
					state = 4;
				} else {
					throw new NumberFormatException(input);
				}
				break;

    		default:
    			throw new NumberFormatException(input);
			}
			
		}

		if (!haveRealPart) {
			z.m_re = Double.valueOf(input).doubleValue();
			return z;
		} else {
			throw new NumberFormatException(input);
		}
	}

}
