import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ValidateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import java.util.Set;

@Slf4j
public class ValidatorUtils {

	private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();

	//static Log log = Log4j2LogFactory.get(ValidatorUtils.class);

	private ValidatorUtils() {}

	/**
	 * errorCode: 数据校验不通过时的错误码
	 */
	public static <T> void validate(T obj, String errorCode) throws ValidateException {
		Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
		if (CollectionUtil.isNotEmpty(set)) {
			ConstraintViolation<T> violation = set.iterator().next();
			log.error("参数校验不通过：{}, {}",  violation.getPropertyPath() ,violation.getMessage());
			throw new ValidateException(errorCode, violation.getMessage());
		}
	}

	/**
	 * errorCode: 数据校验不通过时的错误码
	 */
	public static <T> void validate(T obj, String errorCode,Class v) throws ValidateException {
		Set<ConstraintViolation<T>> set = validator.validate(obj, v);
		if (CollectionUtil.isNotEmpty(set)) {
			ConstraintViolation<T> violation = set.iterator().next();
			log.error("参数校验不通过：{}, {}",  violation.getPropertyPath() ,violation.getMessage());
			throw new ValidateException(errorCode, violation.getMessage());
		}
	}
}
