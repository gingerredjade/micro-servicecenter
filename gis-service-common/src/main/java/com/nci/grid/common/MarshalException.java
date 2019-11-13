/**
 *   Copyright(c) 2009-2015 GIS Department, NCI. All Rights Reserved.
 *
 *     THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF THE
 *                GIS DEPARTMENT, NCI.
 *
 *   The copyright notice above does not evidence any actual or intended
 *   publication of such source code.
 *
 *   Version 1.2 2009.06.18 07:54
 */
package com.nci.grid.common;

/**
 * @author Bright
 */
@SuppressWarnings("serial")
public class MarshalException extends Exception
{
     public MarshalException()
    {
    }

    public MarshalException(String msg, Exception ex)
    {
       super(msg, ex);
    }
}

