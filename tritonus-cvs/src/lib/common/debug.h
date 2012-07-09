/*
 *	debug.h
 */

/*
 *  Copyright (c) 1999 - 2006 by Matthias Pfisterer
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

#ifndef _DEBUG_H
#define _DEBUG_H

#include <stdio.h>


#define FALSE 0
#define TRUE 1

#ifndef _MSC_VER
#define VARIADIC_MACROS
#elif _MSC_VER >= 1400
#define VARIADIC_MACROS
#endif

#ifdef VARIADIC_MACROS
#define out(...) if (debug_flag) { fprintf(debug_file, __VA_ARGS__); \
									fflush(debug_file); }
#endif
static int	debug_flag = FALSE;
static FILE*	debug_file = NULL;


#endif /* _DEBUG_H */

/*** debug.h ***/
