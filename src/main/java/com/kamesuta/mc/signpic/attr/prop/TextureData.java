package com.kamesuta.mc.signpic.attr.prop;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;

import com.kamesuta.mc.bnnwidget.ShortestFloatFormatter;
import com.kamesuta.mc.signpic.attr.IPropBuilder;
import com.kamesuta.mc.signpic.attr.IPropInterpolatable;

public interface TextureData {
	public static final float defaultUV = 0f;
	// Width Height
	public static final float defaultWH = 1f;
	// Crossing Slitting
	public static final float defaultCS = 1f;
	public static final float defaultOpacity = 10f;
	public static final boolean defaultRepeat = true;
	public static final boolean defaultMipMap = true;

	public static class TextureFloat implements IPropInterpolatable<TextureFloat> {
		public final TextureFloatType type;
		public final float data;

		public TextureFloat(final TextureFloatType type, final float data) {
			this.type = type;
			this.data = data;
		}

		@Override
		public TextureFloat per() {
			return this;
		}

		@Override
		public TextureFloat per(final float per, final TextureFloat before) {
			return new TextureFloat(this.type, this.data*per+before.data*(1f-per));
		}

		public static enum TextureFloatType {
			U("u", defaultUV), V("v", defaultUV), W("w", defaultWH), H("h", defaultWH), C("c", defaultCS), S("s", defaultCS), O("o", defaultOpacity), B("b", defaultOpacity), D("d", defaultOpacity),
			;

			public final String identifier;
			public final float defaultValue;

			TextureFloatType(final String identifier, final float defaultValue) {
				this.identifier = identifier;
				this.defaultValue = defaultValue;
			}
		}

		public static class TextureFloatBuilder implements IPropBuilder<TextureFloat, TextureFloat> {
			private final TextureFloatType type;
			private float data;

			public TextureFloatBuilder(final TextureFloatType type) {
				Validate.notNull(type);
				this.type = type;
				this.data = type.defaultValue;
			}

			@Override
			public TextureFloat diff(final TextureFloat base) {
				if (base==null)
					return new TextureFloat(this.type, this.data);
				else
					return new TextureFloat(this.type, base.data+this.data);
			}

			@Override
			public boolean parse(final String src, final String key, final String value) {
				if (StringUtils.equals(key, this.type.identifier)) {
					this.data = NumberUtils.toFloat(value, this.type.defaultValue);
					return true;
				}
				return false;
			}

			@Override
			public String compose() {
				if (this.data!=this.type.defaultValue)
					return this.type.identifier+ShortestFloatFormatter.format(this.data);
				return "";
			}
		}
	}

	public static class TextureBoolean implements IPropInterpolatable<TextureBoolean> {
		public final TextureBooleanType type;
		public final boolean data;

		public TextureBoolean(final TextureBooleanType type, final boolean data) {
			this.type = type;
			this.data = data;
		}

		@Override
		public TextureBoolean per() {
			return this;
		}

		@Override
		public TextureBoolean per(final float per, final TextureBoolean before) {
			return this;
		}

		public static enum TextureBooleanType {
			R("r", defaultRepeat), M("m", defaultMipMap),
			;

			public final String identifier;
			public final boolean defaultValue;

			TextureBooleanType(final String identifier, final boolean defaultValue) {
				this.identifier = identifier;
				this.defaultValue = defaultValue;
			}
		}

		public static class TextureMapBooleanBuilder implements IPropBuilder<TextureBoolean, TextureBoolean> {
			private final TextureBooleanType type;
			private boolean data;

			public TextureMapBooleanBuilder(final TextureBooleanType type) {
				this.type = type;
				this.data = type.defaultValue;
			}

			@Override
			public TextureBoolean diff(final TextureBoolean base) {
				return new TextureBoolean(this.type, this.data);
			}

			@Override
			public boolean parse(final String src, final String key, final String value) {
				if (StringUtils.equals(key, this.type.identifier)) {
					this.data = !this.type.defaultValue;
					return true;
				}
				return false;
			}

			@Override
			public String compose() {
				if (this.data!=this.type.defaultValue)
					return this.type.identifier;
				return "";
			}
		}
	}
}
