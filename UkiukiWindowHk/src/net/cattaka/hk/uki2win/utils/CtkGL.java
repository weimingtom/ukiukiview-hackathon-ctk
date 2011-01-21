package net.cattaka.hk.uki2win.utils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import net.cattaka.hk.uki2win.math.CtkMath;

public class CtkGL implements GL, GL10, GL10Ext, GL11, GL11Ext {
	static class FrustumfArgs {
		private float left;
		private float right;
		private float bottom;
		private float top;
		private float zNear;
		//private float zFar;
	}

	private GL10 mgl;
	private GL10Ext mgl10Ext;
	private GL11 mgl11;
	private GL11Ext mgl11Ext;

	private FrustumfArgs frustumfArgs = new FrustumfArgs();
	private float[] cameraMatrix = CtkMath.createMatrix4f();

	public CtkGL(GL gl) {
		mgl = (GL10) gl;
		if (gl instanceof GL10Ext) {
			mgl10Ext = (GL10Ext) gl;
		}
		if (gl instanceof GL11) {
			mgl11 = (GL11) gl;
		}
		if (gl instanceof GL11Ext) {
			mgl11Ext = (GL11Ext) gl;
		}
	}
	
	//Ex methods
	public void loadCameraMatrix(float[] m) {
		CtkMath.copyMatrix4f(this.cameraMatrix, m);
		mgl.glLoadMatrixf(m, 0);
	}
	
	public void calcPickRay(float[] dstPos, float[] dstDir, float x, float y) {
		float[] startPos = CtkMath.createVector4f(0,0,0,1);
		float[] endPos = CtkMath.createVector4f(0,0,0,1);
		endPos[0] = frustumfArgs.left + (frustumfArgs.right - frustumfArgs.left) * x;
		endPos[1] = frustumfArgs.top + (frustumfArgs.bottom - frustumfArgs.top) * y;
		endPos[2] = -frustumfArgs.zNear;
		float[] cm = CtkMath.createMatrix4f();
		CtkMath.inverseMatrix4f(cm, cameraMatrix);
		CtkMath.transposeEq3F(startPos, cm);
		CtkMath.transposeEq3F(endPos, cm);
		CtkMath.copy3F(dstPos, startPos);
		CtkMath.sub3F(dstDir, endPos, startPos);
	}
	
	// GL10 mgl;
	public void glActiveTexture(int texture) {
		mgl.glActiveTexture(texture);
	}

	public void glAlphaFunc(int func, float ref) {
		mgl.glAlphaFunc(func, ref);
	}

	public void glAlphaFuncx(int func, int ref) {
		mgl.glAlphaFuncx(func, ref);
	}

	public void glBindTexture(int target, int texture) {
		mgl.glBindTexture(target, texture);
	}

	public void glBlendFunc(int sfactor, int dfactor) {
		mgl.glBlendFunc(sfactor, dfactor);
	}

	public void glClear(int mask) {
		mgl.glClear(mask);
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		mgl.glClearColor(red, green, blue, alpha);
	}

	public void glClearColorx(int red, int green, int blue, int alpha) {
		mgl.glClearColorx(red, green, blue, alpha);
	}

	public void glClearDepthf(float depth) {
		mgl.glClearDepthf(depth);
	}

	public void glClearDepthx(int depth) {
		mgl.glClearDepthx(depth);
	}

	public void glClearStencil(int s) {
		mgl.glClearStencil(s);
	}

	public void glClientActiveTexture(int texture) {
		mgl.glClientActiveTexture(texture);
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		mgl.glColor4f(red, green, blue, alpha);
	}

	public void glColor4x(int red, int green, int blue, int alpha) {
		mgl.glColor4x(red, green, blue, alpha);
	}

	public void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		mgl.glColorMask(red, green, blue, alpha);
	}

	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		mgl.glColorPointer(size, type, stride, pointer);
	}

	public void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		mgl.glCompressedTexImage2D(target, level, internalformat, width,
				height, border, imageSize, data);
	}

	public void glCompressedTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int imageSize,
			Buffer data) {
		mgl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width,
				height, format, imageSize, data);
	}

	public void glCopyTexImage2D(int target, int level, int internalformat,
			int x, int y, int width, int height, int border) {
		mgl.glCopyTexImage2D(target, level, internalformat, x, y, width,
				height, border);
	}

	public void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height) {
		mgl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width,
				height);
	}

	public void glCullFace(int mode) {
		mgl.glCullFace(mode);
	}

	public void glDeleteTextures(int n, int[] textures, int offset) {
		mgl.glDeleteTextures(n, textures, offset);
	}

	public void glDeleteTextures(int n, IntBuffer textures) {
		mgl.glDeleteTextures(n, textures);
	}

	public void glDepthFunc(int func) {
		mgl.glDepthFunc(func);
	}

	public void glDepthMask(boolean flag) {
		mgl.glDepthMask(flag);
	}

	public void glDepthRangef(float zNear, float zFar) {
		mgl.glDepthRangef(zNear, zFar);
	}

	public void glDepthRangex(int zNear, int zFar) {
		mgl.glDepthRangex(zNear, zFar);
	}

	public void glDisable(int cap) {
		mgl.glDisable(cap);
	}

	public void glDisableClientState(int array) {
		mgl.glDisableClientState(array);
	}

	public void glDrawArrays(int mode, int first, int count) {
		mgl.glDrawArrays(mode, first, count);
	}

	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		mgl.glDrawElements(mode, count, type, indices);
	}

	public void glEnable(int cap) {
		mgl.glEnable(cap);
	}

	public void glEnableClientState(int array) {
		mgl.glEnableClientState(array);
	}

	public void glFinish() {
		mgl.glFinish();
	}

	public void glFlush() {
		mgl.glFlush();
	}

	public void glFogf(int pname, float param) {
		mgl.glFogf(pname, param);
	}

	public void glFogfv(int pname, float[] params, int offset) {
		mgl.glFogfv(pname, params, offset);
	}

	public void glFogfv(int pname, FloatBuffer params) {
		mgl.glFogfv(pname, params);
	}

	public void glFogx(int pname, int param) {
		mgl.glFogx(pname, param);
	}

	public void glFogxv(int pname, int[] params, int offset) {
		mgl.glFogxv(pname, params, offset);
	}

	public void glFogxv(int pname, IntBuffer params) {
		mgl.glFogxv(pname, params);
	}

	public void glFrontFace(int mode) {
		mgl.glFrontFace(mode);
	}

	public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
		this.frustumfArgs.left = left;
		this.frustumfArgs.right = right;
		this.frustumfArgs.bottom = bottom;
		this.frustumfArgs.top = top;
		this.frustumfArgs.zNear = zNear;
		//this.frustumfArgs.zFar = zFar;
		mgl.glFrustumf(left, right, bottom, top, zNear, zFar);
	}

	public void glFrustumx(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		mgl.glFrustumx(left, right, bottom, top, zNear, zFar);
	}

	public void glGenTextures(int n, int[] textures, int offset) {
		mgl.glGenTextures(n, textures, offset);
	}

	public void glGenTextures(int n, IntBuffer textures) {
		mgl.glGenTextures(n, textures);
	}

	public int glGetError() {
		return mgl.glGetError();
	}

	public void glGetIntegerv(int pname, int[] params, int offset) {
		mgl.glGetIntegerv(pname, params, offset);
	}

	public void glGetIntegerv(int pname, IntBuffer params) {
		mgl.glGetIntegerv(pname, params);
	}

	public String glGetString(int name) {
		return mgl.glGetString(name);
	}

	public void glHint(int target, int mode) {
		mgl.glHint(target, mode);
	}

	public void glLightf(int light, int pname, float param) {
		mgl.glLightf(light, pname, param);
	}

	public void glLightfv(int light, int pname, float[] params, int offset) {
		mgl.glLightfv(light, pname, params, offset);
	}

	public void glLightfv(int light, int pname, FloatBuffer params) {
		mgl.glLightfv(light, pname, params);
	}

	public void glLightModelf(int pname, float param) {
		mgl.glLightModelf(pname, param);
	}

	public void glLightModelfv(int pname, float[] params, int offset) {
		mgl.glLightModelfv(pname, params, offset);
	}

	public void glLightModelfv(int pname, FloatBuffer params) {
		mgl.glLightModelfv(pname, params);
	}

	public void glLightModelx(int pname, int param) {
		mgl.glLightModelx(pname, param);
	}

	public void glLightModelxv(int pname, int[] params, int offset) {
		mgl.glLightModelxv(pname, params, offset);
	}

	public void glLightModelxv(int pname, IntBuffer params) {
		mgl.glLightModelxv(pname, params);
	}

	public void glLightx(int light, int pname, int param) {
		mgl.glLightx(light, pname, param);
	}

	public void glLightxv(int light, int pname, int[] params, int offset) {
		mgl.glLightxv(light, pname, params, offset);
	}

	public void glLightxv(int light, int pname, IntBuffer params) {
		mgl.glLightxv(light, pname, params);
	}

	public void glLineWidth(float width) {
		mgl.glLineWidth(width);
	}

	public void glLineWidthx(int width) {
		mgl.glLineWidthx(width);
	}

	public void glLoadIdentity() {
		mgl.glLoadIdentity();
	}

	public void glLoadMatrixf(float[] m, int offset) {
		mgl.glLoadMatrixf(m, offset);
	}

	public void glLoadMatrixf(FloatBuffer m) {
		mgl.glLoadMatrixf(m);
	}

	public void glLoadMatrixx(int[] m, int offset) {
		mgl.glLoadMatrixx(m, offset);
	}

	public void glLoadMatrixx(IntBuffer m) {
		mgl.glLoadMatrixx(m);
	}

	public void glLogicOp(int opcode) {
		mgl.glLogicOp(opcode);
	}

	public void glMaterialf(int face, int pname, float param) {
		mgl.glMaterialf(face, pname, param);
	}

	public void glMaterialfv(int face, int pname, float[] params, int offset) {
		mgl.glMaterialfv(face, pname, params, offset);
	}

	public void glMaterialfv(int face, int pname, FloatBuffer params) {
		mgl.glMaterialfv(face, pname, params);
	}

	public void glMaterialx(int face, int pname, int param) {
		mgl.glMaterialx(face, pname, param);
	}

	public void glMaterialxv(int face, int pname, int[] params, int offset) {
		mgl.glMaterialxv(face, pname, params, offset);
	}

	public void glMaterialxv(int face, int pname, IntBuffer params) {
		mgl.glMaterialxv(face, pname, params);
	}

	public void glMatrixMode(int mode) {
		mgl.glMatrixMode(mode);
	}

	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		mgl.glMultiTexCoord4f(target, s, t, r, q);
	}

	public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
		mgl.glMultiTexCoord4x(target, s, t, r, q);
	}

	public void glMultMatrixf(float[] m, int offset) {
		mgl.glMultMatrixf(m, offset);
	}

	public void glMultMatrixf(FloatBuffer m) {
		mgl.glMultMatrixf(m);
	}

	public void glMultMatrixx(int[] m, int offset) {
		mgl.glMultMatrixx(m, offset);
	}

	public void glMultMatrixx(IntBuffer m) {
		mgl.glMultMatrixx(m);
	}

	public void glNormal3f(float nx, float ny, float nz) {
		mgl.glNormal3f(nx, ny, nz);
	}

	public void glNormal3x(int nx, int ny, int nz) {
		mgl.glNormal3x(nx, ny, nz);
	}

	public void glNormalPointer(int type, int stride, Buffer pointer) {
		mgl.glNormalPointer(type, stride, pointer);
	}

	public void glOrthof(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		mgl.glOrthof(left, right, bottom, top, zNear, zFar);
	}

	public void glOrthox(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		mgl.glOrthox(left, right, bottom, top, zNear, zFar);
	}

	public void glPixelStorei(int pname, int param) {
		mgl.glPixelStorei(pname, param);
	}

	public void glPointSize(float size) {
		mgl.glPointSize(size);
	}

	public void glPointSizex(int size) {
		mgl.glPointSizex(size);
	}

	public void glPolygonOffset(float factor, float units) {
		mgl.glPolygonOffset(factor, units);
	}

	public void glPolygonOffsetx(int factor, int units) {
		mgl.glPolygonOffsetx(factor, units);
	}

	public void glPopMatrix() {
		mgl.glPopMatrix();
	}

	public void glPushMatrix() {
		mgl.glPushMatrix();
	}

	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, Buffer pixels) {
		mgl.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public void glRotatef(float angle, float x, float y, float z) {
		mgl.glRotatef(angle, x, y, z);
	}

	public void glRotatex(int angle, int x, int y, int z) {
		mgl.glRotatex(angle, x, y, z);
	}

	public void glSampleCoverage(float value, boolean invert) {
		mgl.glSampleCoverage(value, invert);
	}

	public void glSampleCoveragex(int value, boolean invert) {
		mgl.glSampleCoveragex(value, invert);
	}

	public void glScalef(float x, float y, float z) {
		mgl.glScalef(x, y, z);
	}

	public void glScalex(int x, int y, int z) {
		mgl.glScalex(x, y, z);
	}

	public void glScissor(int x, int y, int width, int height) {
		mgl.glScissor(x, y, width, height);
	}

	public void glShadeModel(int mode) {
		mgl.glShadeModel(mode);
	}

	public void glStencilFunc(int func, int ref, int mask) {
		mgl.glStencilFunc(func, ref, mask);
	}

	public void glStencilMask(int mask) {
		mgl.glStencilMask(mask);
	}

	public void glStencilOp(int fail, int zfail, int zpass) {
		mgl.glStencilOp(fail, zfail, zpass);
	}

	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		mgl.glTexCoordPointer(size, type, stride, pointer);
	}

	public void glTexEnvf(int target, int pname, float param) {
		mgl.glTexEnvf(target, pname, param);
	}

	public void glTexEnvfv(int target, int pname, float[] params, int offset) {
		mgl.glTexEnvfv(target, pname, params, offset);
	}

	public void glTexEnvfv(int target, int pname, FloatBuffer params) {
		mgl.glTexEnvfv(target, pname, params);
	}

	public void glTexEnvx(int target, int pname, int param) {
		mgl.glTexEnvx(target, pname, param);
	}

	public void glTexEnvxv(int target, int pname, int[] params, int offset) {
		mgl.glTexEnvxv(target, pname, params, offset);
	}

	public void glTexEnvxv(int target, int pname, IntBuffer params) {
		mgl.glTexEnvxv(target, pname, params);
	}

	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels) {
		mgl.glTexImage2D(target, level, internalformat, width, height, border,
				format, type, pixels);
	}

	public void glTexParameterf(int target, int pname, float param) {
		mgl.glTexParameterf(target, pname, param);
	}

	public void glTexParameterx(int target, int pname, int param) {
		mgl.glTexParameterx(target, pname, param);
	}

	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels) {
		mgl.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
				format, type, pixels);
	}

	public void glTranslatef(float x, float y, float z) {
		mgl.glTranslatef(x, y, z);
	}

	public void glTranslatex(int x, int y, int z) {
		mgl.glTranslatex(x, y, z);
	}

	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		mgl.glVertexPointer(size, type, stride, pointer);
	}

	public void glViewport(int x, int y, int width, int height) {
		mgl.glViewport(x, y, width, height);
	}

	// GL10Ext mgl10Ext;
	public int glQueryMatrixxOES(int[] mantissa, int mantissaOffset,
			int[] exponent, int exponentOffset) {
		return mgl10Ext.glQueryMatrixxOES(mantissa, mantissaOffset, exponent,
				exponentOffset);
	}

	public int glQueryMatrixxOES(IntBuffer mantissa, IntBuffer exponent) {
		return mgl10Ext.glQueryMatrixxOES(mantissa, exponent);
	}

	// GL11 mgl11;
	public void glBindBuffer(int arg0, int arg1) {
		mgl11.glBindBuffer(arg0, arg1);
	}

	public void glBufferData(int target, int size, Buffer data, int usage) {
		mgl11.glBufferData(target, size, data, usage);
	}

	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		mgl11.glBufferSubData(target, offset, size, data);
	}

	public void glClipPlanef(int plane, float[] equation, int offset) {
		mgl11.glClipPlanef(plane, equation, offset);
	}

	public void glClipPlanef(int plane, FloatBuffer equation) {
		mgl11.glClipPlanef(plane, equation);
	}

	public void glClipPlanex(int plane, int[] equation, int offset) {
		mgl11.glClipPlanex(plane, equation, offset);
	}

	public void glClipPlanex(int plane, IntBuffer equation) {
		mgl11.glClipPlanex(plane, equation);
	}

	public void glColor4ub(byte red, byte green, byte blue, byte alpha) {
		mgl11.glColor4ub(red, green, blue, alpha);
	}

	public void glColorPointer(int size, int type, int stride, int offset) {
		mgl11.glColorPointer(size, type, stride, offset);
	}

	public void glDeleteBuffers(int n, int[] buffers, int offset) {
		mgl11.glDeleteBuffers(n, buffers, offset);
	}

	public void glDeleteBuffers(int n, IntBuffer buffers) {
		mgl11.glDeleteBuffers(n, buffers);
	}

	public void glDrawElements(int mode, int count, int type, int offset) {
		mgl11.glDrawElements(mode, count, type, offset);
	}

	public void glGenBuffers(int n, int[] buffers, int offset) {
		mgl11.glGenBuffers(n, buffers, offset);
	}

	public void glGenBuffers(int n, IntBuffer buffers) {
		mgl11.glGenBuffers(n, buffers);
	}

	public void glGetBooleanv(int pname, boolean[] params, int offset) {
		mgl11.glGetBooleanv(pname, params, offset);
	}

	public void glGetBooleanv(int pname, IntBuffer params) {
		mgl11.glGetBooleanv(pname, params);
	}

	public void glGetBufferParameteriv(int target, int pname, int[] params,
			int offset) {
		mgl11.glGetBufferParameteriv(target, pname, params, offset);
	}

	public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		mgl11.glGetBufferParameteriv(target, pname, params);
	}

	public void glGetClipPlanef(int pname, float[] eqn, int offset) {
		mgl11.glGetClipPlanef(pname, eqn, offset);
	}

	public void glGetClipPlanef(int pname, FloatBuffer eqn) {
		mgl11.glGetClipPlanef(pname, eqn);
	}

	public void glGetClipPlanex(int pname, int[] eqn, int offset) {
		mgl11.glGetClipPlanex(pname, eqn, offset);
	}

	public void glGetClipPlanex(int pname, IntBuffer eqn) {
		mgl11.glGetClipPlanex(pname, eqn);
	}

	public void glGetFixedv(int pname, int[] params, int offset) {
		mgl11.glGetFixedv(pname, params, offset);
	}

	public void glGetFixedv(int pname, IntBuffer params) {
		mgl11.glGetFixedv(pname, params);
	}

	public void glGetFloatv(int pname, float[] params, int offset) {
		mgl11.glGetFloatv(pname, params, offset);
	}

	public void glGetFloatv(int pname, FloatBuffer params) {
		mgl11.glGetFloatv(pname, params);
	}

	public void glGetLightfv(int light, int pname, float[] params, int offset) {
		mgl11.glGetLightfv(light, pname, params, offset);
	}

	public void glGetLightfv(int light, int pname, FloatBuffer params) {
		mgl11.glGetLightfv(light, pname, params);
	}

	public void glGetLightxv(int light, int pname, int[] params, int offset) {
		mgl11.glGetLightxv(light, pname, params, offset);
	}

	public void glGetLightxv(int light, int pname, IntBuffer params) {
		mgl11.glGetLightxv(light, pname, params);
	}

	public void glGetMaterialfv(int face, int pname, float[] params, int offset) {
		mgl11.glGetMaterialfv(face, pname, params, offset);
	}

	public void glGetMaterialfv(int face, int pname, FloatBuffer params) {
		mgl11.glGetMaterialfv(face, pname, params);
	}

	public void glGetMaterialxv(int face, int pname, int[] params, int offset) {
		mgl11.glGetMaterialxv(face, pname, params, offset);
	}

	public void glGetMaterialxv(int face, int pname, IntBuffer params) {
		mgl11.glGetMaterialxv(face, pname, params);
	}

	public void glGetPointerv(int pname, Buffer[] params) {
		mgl11.glGetPointerv(pname, params);
	}

	public void glGetTexEnviv(int env, int pname, int[] params, int offset) {
		mgl11.glGetTexEnviv(env, pname, params, offset);
	}

	public void glGetTexEnviv(int env, int pname, IntBuffer params) {
		mgl11.glGetTexEnviv(env, pname, params);
	}

	public void glGetTexEnvxv(int env, int pname, int[] params, int offset) {
		mgl11.glGetTexEnvxv(env, pname, params, offset);
	}

	public void glGetTexEnvxv(int env, int pname, IntBuffer params) {
		mgl11.glGetTexEnvxv(env, pname, params);
	}

	public void glGetTexParameterfv(int target, int pname, float[] params,
			int offset) {
		mgl11.glGetTexParameterfv(target, pname, params, offset);
	}

	public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		mgl11.glGetTexParameterfv(target, pname, params);
	}

	public void glGetTexParameteriv(int target, int pname, int[] params,
			int offset) {
		mgl11.glGetTexParameteriv(target, pname, params, offset);
	}

	public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		mgl11.glGetTexParameteriv(target, pname, params);
	}

	public void glGetTexParameterxv(int target, int pname, int[] params,
			int offset) {
		mgl11.glGetTexParameterxv(target, pname, params, offset);
	}

	public void glGetTexParameterxv(int target, int pname, IntBuffer params) {
		mgl11.glGetTexParameterxv(target, pname, params);
	}

	public boolean glIsBuffer(int buffer) {
		return mgl11.glIsBuffer(buffer);
	}

	public boolean glIsEnabled(int cap) {
		return mgl11.glIsEnabled(cap);
	}

	public boolean glIsTexture(int texture) {
		return mgl11.glIsTexture(texture);
	}

	public void glNormalPointer(int type, int stride, int offset) {
		mgl11.glNormalPointer(type, stride, offset);
	}

	public void glPointParameterf(int pname, float param) {
		mgl11.glPointParameterf(pname, param);
	}

	public void glPointParameterfv(int pname, float[] params, int offset) {
		mgl11.glPointParameterfv(pname, params, offset);
	}

	public void glPointParameterfv(int pname, FloatBuffer params) {
		mgl11.glPointParameterfv(pname, params);
	}

	public void glPointParameterx(int pname, int param) {
		mgl11.glPointParameterx(pname, param);
	}

	public void glPointParameterxv(int pname, int[] params, int offset) {
		mgl11.glPointParameterxv(pname, params, offset);
	}

	public void glPointParameterxv(int pname, IntBuffer params) {
		mgl11.glPointParameterxv(pname, params);
	}

	public void glPointSizePointerOES(int type, int stride, Buffer pointer) {
		mgl11.glPointSizePointerOES(type, stride, pointer);
	}

	public void glTexCoordPointer(int size, int type, int stride, int offset) {
		mgl11.glTexCoordPointer(size, type, stride, offset);
	}

	public void glTexEnvi(int target, int pname, int param) {
		mgl11.glTexEnvi(target, pname, param);
	}

	public void glTexEnviv(int target, int pname, int[] params, int offset) {
		mgl11.glTexEnviv(target, pname, params, offset);
	}

	public void glTexEnviv(int target, int pname, IntBuffer params) {
		mgl11.glTexEnviv(target, pname, params);
	}

	public void glTexParameterfv(int target, int pname, float[] params,
			int offset) {
		mgl11.glTexParameterfv(target, pname, params, offset);
	}

	public void glTexParameterfv(int target, int pname, FloatBuffer params) {
		mgl11.glTexParameterfv(target, pname, params);
	}

	public void glTexParameteri(int target, int pname, int param) {
		mgl11.glTexParameteri(target, pname, param);
	}

	public void glTexParameteriv(int target, int pname, int[] params, int offset) {
		mgl11.glTexParameteriv(target, pname, params, offset);
	}

	public void glTexParameteriv(int target, int pname, IntBuffer params) {
		mgl11.glTexParameteriv(target, pname, params);
	}

	public void glTexParameterxv(int target, int pname, int[] params, int offset) {
		mgl11.glTexParameterxv(target, pname, params, offset);
	}

	public void glTexParameterxv(int target, int pname, IntBuffer params) {
		mgl11.glTexParameterxv(target, pname, params);
	}

	public void glVertexPointer(int size, int type, int stride, int offset) {
		mgl11.glVertexPointer(size, type, stride, offset);
	}

	// GL11Ext mgl11Ext;
	public void glCurrentPaletteMatrixOES(int matrixpaletteindex) {
		mgl11Ext.glCurrentPaletteMatrixOES(matrixpaletteindex);
	}

	public void glDrawTexfOES(float x, float y, float z, float width,
			float height) {
		mgl11Ext.glDrawTexfOES(x, y, z, width, height);
	}

	public void glDrawTexfvOES(float[] coords, int offset) {
		mgl11Ext.glDrawTexfvOES(coords, offset);
	}

	public void glDrawTexfvOES(FloatBuffer coords) {
		mgl11Ext.glDrawTexfvOES(coords);
	}

	public void glDrawTexiOES(int x, int y, int z, int width, int height) {
		mgl11Ext.glDrawTexiOES(x, y, z, width, height);
	}

	public void glDrawTexivOES(int[] coords, int offset) {
		mgl11Ext.glDrawTexivOES(coords, offset);
	}

	public void glDrawTexivOES(IntBuffer coords) {
		mgl11Ext.glDrawTexivOES(coords);
	}

	public void glDrawTexsOES(short x, short y, short z, short width,
			short height) {
		mgl11Ext.glDrawTexsOES(x, y, z, width, height);
	}

	public void glDrawTexsvOES(short[] coords, int offset) {
		mgl11Ext.glDrawTexsvOES(coords, offset);
	}

	public void glDrawTexsvOES(ShortBuffer coords) {
		mgl11Ext.glDrawTexsvOES(coords);
	}

	public void glDrawTexxOES(int x, int y, int z, int width, int height) {
		mgl11Ext.glDrawTexxOES(x, y, z, width, height);
	}

	public void glDrawTexxvOES(int[] coords, int offset) {
		mgl11Ext.glDrawTexxvOES(coords, offset);
	}

	public void glDrawTexxvOES(IntBuffer coords) {
		mgl11Ext.glDrawTexxvOES(coords);
	}

	public void glLoadPaletteFromModelViewMatrixOES() {
		mgl11Ext.glLoadPaletteFromModelViewMatrixOES();
	}

	public void glMatrixIndexPointerOES(int size, int type, int stride,
			Buffer pointer) {
		mgl11Ext.glMatrixIndexPointerOES(size, type, stride, pointer);
	}

	public void glMatrixIndexPointerOES(int size, int type, int stride,
			int offset) {
		mgl11Ext.glMatrixIndexPointerOES(size, type, stride, offset);
	}

	public void glWeightPointerOES(int size, int type, int stride,
			Buffer pointer) {
		mgl11Ext.glWeightPointerOES(size, type, stride, pointer);
	}

	public void glWeightPointerOES(int size, int type, int stride, int offset) {
		mgl11Ext.glWeightPointerOES(size, type, stride, offset);
	}
}
