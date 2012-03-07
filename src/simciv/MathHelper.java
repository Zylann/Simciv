package simciv;

public class MathHelper
{
    static float smoothCurve(float x)
    {
        return (float) (6 * Math.pow(x,5) - 15 * Math.pow(x, 4) + 10 * Math.pow(x,3));
    }
    
    static float linearInterpolation(float x0, float x1, float t)
    {
        return x0 + (x1 - x0) * t;
    }
    
    static float biLinearInterpolation(
			float x0y0, float x1y0,
			float x0y1, float x1y1,
			float x, float y)
    {
        float tx = smoothCurve(x);
        float ty = smoothCurve(y);

        float u = linearInterpolation(x0y0,x1y0,tx);
        float v = linearInterpolation(x0y1,x1y1,tx);

        return linearInterpolation(u,v,ty);
    }
}
