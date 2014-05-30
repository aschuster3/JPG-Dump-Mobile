package com.jpgdump.mobile.util;

import java.util.Random;

import com.jpgdump.mobile.objects.Comment;

public class CommentColors
{
    public static int[] genCommentColor(int postId, Comment comment)
    {
        Random rand = new Random(postId);
        
        double  rr = rand.nextInt() / 32767.0,
                ccolor;
        if(Integer.parseInt(comment.getId()) > 84681)
        {
            ccolor = (getPos(Integer.parseInt(comment.getOrdinal()) - 1) + rr) % 1;
        }
        else
        {
            ccolor = (getPos(Integer.parseInt(comment.getOrdinal()) - 1) + ((postId & 0xF) / 16)) % 1;
        }
        
        return genColor(ccolor);
    }
    
    private static double getPos(int num)
    {
        double pos = 0;
        if(num != 0)
        {
            int b = (int)(Math.log(num) / Math.log(2));
            num = num & ((1 << b) - 1);
            
            int rev = 0;
            for(int i = 0; i < b; i++)
            {
                rev |= ((num >> i) & 1) << (b - i);
            }
            
            pos = (rev + 1) / (2 << b);
        }
        return pos;
    }
    
    
    private static int[] hsl_to_rgb(int[] hsl)
    {
        int[] rgb = new int[3];
        double  s = hsl[1] / 255.0,
                l = hsl[2] / 255.0,
                c = (1 - Math.abs(2 * l - 1)) * s,
                hp = hsl[0] / (256 / 6.0),
                x = c * (1 - Math.abs(hp % 2) - 1);
        
        switch((int) hp)
        {
            case 0:
                rgb[0] = (int) Math.round(c);
                rgb[1] = (int) Math.round(x);
                break;
            case 1:
                rgb[0] = (int) Math.round(x);
                rgb[1] = (int) Math.round(c);
                break;
            case 2:
                rgb[1] = (int) Math.round(c);
                rgb[2] = (int) Math.round(x);
                break;
            case 3:
                rgb[1] = (int) Math.round(x);
                rgb[2] = (int) Math.round(c);
                break;
            case 4:
                rgb[0] = (int) Math.round(x);
                rgb[2] = (int) Math.round(c);
            case 5:
                rgb[0] = (int) Math.round(c);
                rgb[2] = (int) Math.round(x);
            default:
        }
        
        double m = l - c / 2;
        for(int i = 0; i < 3; i++)
        {
            rgb[i] = (int)((rgb[i] + m) * 255);
        }
        return rgb;
    }
    
    private static int[] genColor(double seed)
    {
        int[] arr = new int[3];
        seed = 1 - seed;
        
        arr[0] = (int) Math.round(seed * 255);
        arr[1] = 255;
        arr[2] = 144;
        
        return hsl_to_rgb(arr);
    }
}
