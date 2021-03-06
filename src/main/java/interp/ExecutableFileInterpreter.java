package interp;

import interp.customoperations.CustomOperationsList;
import interp.ints.Int64CustomOperations;
import interp.ints.NativeIntCustomOperations;
import interp.io.ChannelRegistry;
import interp.primitives.*;
import interp.stack.ValueStack;
import interp.value.*;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static interp.Interpreter.valTrue;

public class ExecutableFileInterpreter {
    private final ExecutableBuilder exb;
    private final PrimitiveRegistry primitiveRegistry;
    private final ValueStack stack = new ValueStack();
    private final CamlState camlState = new CamlState();

    public ExecutableFileInterpreter() throws IOException {
        this(new ChannelRegistry());
    }

    public ExecutableFileInterpreter(ChannelRegistry channelRegistry) throws IOException {
//        final Sys sys = new Sys("exec", new String[0]);
        final Sys sys = new Sys("exec", new String[]{"-c", "./misc/simple.ml"});
        OOIdGenerator ooIdGenerator = new OOIdGenerator();
        CustomOperationsList customOperationsList = new CustomOperationsList();
        CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
        Intern intern = new Intern(customOperationsList, codeFragmentTable, ooIdGenerator);
        exb = new ExecutableBuilder(codeFragmentTable, intern);
        primitiveRegistry = new PrimitiveRegistry();
        Compare compare = new Compare(camlState);
        Backtrace backtrace = new Backtrace();

        NamedValues namedValues = new NamedValues();

        primitiveRegistry.addFunc1("caml_abs_float", DoubleValue::abs);
        primitiveRegistry.addFunc1("caml_acos_float", DoubleValue::acos);
        primitiveRegistry.unimplemented("caml_add_debug_info");
        primitiveRegistry.addFunc2("caml_add_float", DoubleValue::add);
        primitiveRegistry.addFunc1("caml_alloc_dummy", ExecutableFileInterpreter::allocDummy);
        primitiveRegistry.addFunc1("caml_alloc_dummy_float", Transformer.intValue, Transformer.doubleArray, Dummy::allocDouble);
        primitiveRegistry.addFunc1("caml_alloc_dummy_function", ExecutableFileInterpreter::allocDummy);
        primitiveRegistry.unimplemented("caml_alloc_dummy_infix");
        primitiveRegistry.addFunc2("caml_array_append", BaseArrayValue::append);
        primitiveRegistry.addFuncN("caml_array_blit", (Value[] values) -> BaseArrayValue.blit(
                (BaseArrayValue) values[0],
                (LongValue) values[1],
                (BaseArrayValue) values[2],
                (LongValue) values[3],
                (LongValue) values[4]
        ));
        primitiveRegistry.addFunc1("caml_array_concat", BaseArrayValue::arrayConcat);
        primitiveRegistry.unimplemented("caml_array_fill");
        primitiveRegistry.addFunc2("caml_array_get", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc2("caml_array_get_addr", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc2("caml_array_get_float", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc3("caml_array_set", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc3("caml_array_set_addr", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc3("caml_array_set_float", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc3("caml_array_sub", BaseArrayValue::sub);
        primitiveRegistry.addFunc2("caml_array_unsafe_get", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc2("caml_array_unsafe_get_float", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc3("caml_array_unsafe_set", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc3("caml_array_unsafe_set_addr", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc3("caml_array_unsafe_set_float", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc1("caml_asin_float", DoubleValue::asin);
        primitiveRegistry.addFunc2("caml_atan2_float", DoubleValue::atan2);
        primitiveRegistry.addFunc1("caml_atan_float", DoubleValue::atan);
        primitiveRegistry.unimplemented("caml_ba_blit");
        primitiveRegistry.unimplemented("caml_ba_change_layout");
        primitiveRegistry.unimplemented("caml_ba_create");
        primitiveRegistry.unimplemented("caml_ba_dim");
        primitiveRegistry.unimplemented("caml_ba_dim_1");
        primitiveRegistry.unimplemented("caml_ba_dim_2");
        primitiveRegistry.unimplemented("caml_ba_dim_3");
        primitiveRegistry.unimplemented("caml_ba_fill");
        primitiveRegistry.unimplemented("caml_ba_get_1");
        primitiveRegistry.unimplemented("caml_ba_get_2");
        primitiveRegistry.unimplemented("caml_ba_get_3");
        primitiveRegistry.unimplemented("caml_ba_get_generic");
        primitiveRegistry.unimplemented("caml_ba_kind");
        primitiveRegistry.unimplemented("caml_ba_layout");
        primitiveRegistry.unimplemented("caml_ba_num_dims");
        primitiveRegistry.unimplemented("caml_ba_reshape");
        primitiveRegistry.unimplemented("caml_ba_set_1");
        primitiveRegistry.unimplemented("caml_ba_set_2");
        primitiveRegistry.unimplemented("caml_ba_set_3");
        primitiveRegistry.unimplemented("caml_ba_set_generic");
        primitiveRegistry.unimplemented("caml_ba_slice");
        primitiveRegistry.unimplemented("caml_ba_sub");
        primitiveRegistry.unimplemented("caml_ba_uint8_get16");
        primitiveRegistry.unimplemented("caml_ba_uint8_get32");
        primitiveRegistry.unimplemented("caml_ba_uint8_get64");
        primitiveRegistry.unimplemented("caml_ba_uint8_set16");
        primitiveRegistry.unimplemented("caml_ba_uint8_set32");
        primitiveRegistry.unimplemented("caml_ba_uint8_set64");
        primitiveRegistry.unimplemented("caml_backtrace_status");
        primitiveRegistry.addFuncN("caml_blit_bytes", (Value[] values) -> StringValue.blit(
                (StringValue) values[0],
                (LongValue) values[1],
                (StringValue) values[2],
                (LongValue) values[3],
                (LongValue) values[4]
        ));
        primitiveRegistry.addFuncN("caml_blit_string",
                (Value[] values) -> StringValue.blit(
                        (StringValue) values[0],
                        (LongValue) values[1],
                        (StringValue) values[2],
                        (LongValue) values[3],
                        (LongValue) values[4]
                ));
        primitiveRegistry.unimplemented("caml_bswap16");
        primitiveRegistry.unimplemented("caml_bytes_compare");
        primitiveRegistry.unimplemented("caml_bytes_equal");
        primitiveRegistry.unimplemented("caml_bytes_get");
        primitiveRegistry.unimplemented("caml_bytes_get16");
        primitiveRegistry.unimplemented("caml_bytes_get32");
        primitiveRegistry.unimplemented("caml_bytes_get64");
        primitiveRegistry.unimplemented("caml_bytes_greaterequal");
        primitiveRegistry.unimplemented("caml_bytes_greaterthan");
        primitiveRegistry.unimplemented("caml_bytes_lessequal");
        primitiveRegistry.unimplemented("caml_bytes_lessthan");
        primitiveRegistry.unimplemented("caml_bytes_notequal");
        primitiveRegistry.addFunc1("caml_bytes_of_string", Value::identity);
        primitiveRegistry.unimplemented("caml_bytes_set");
        primitiveRegistry.unimplemented("caml_bytes_set16");
        primitiveRegistry.unimplemented("caml_bytes_set32");
        primitiveRegistry.unimplemented("caml_bytes_set64");
        primitiveRegistry.addFunc1("caml_ceil_float", DoubleValue::ceil);
        primitiveRegistry.unimplemented("caml_channel_descriptor");
        primitiveRegistry.unimplemented("caml_classify_float");
        primitiveRegistry.unimplemented("caml_compare");
        primitiveRegistry.addFunc1Ctx("caml_convert_raw_backtrace", backtrace::convertRawBacktrace);
        primitiveRegistry.addFunc1Ctx("caml_convert_raw_backtrace_slot", backtrace::convertRawBacktraceSlot);
        primitiveRegistry.unimplemented("caml_copysign_float");
        primitiveRegistry.addFunc1("caml_cos_float", DoubleValue::cos);
        primitiveRegistry.addFunc1("caml_cosh_float", DoubleValue::cosh);
        primitiveRegistry.addFunc1("caml_create_bytes", StringValue::createBytes);
        primitiveRegistry.unimplemented("caml_create_string");
        primitiveRegistry.addFunc2("caml_div_float", DoubleValue::div);
        primitiveRegistry.unimplemented("caml_dynlink_add_primitive");
        primitiveRegistry.unimplemented("caml_dynlink_close_lib");
        primitiveRegistry.unimplemented("caml_dynlink_get_current_libs");
        primitiveRegistry.unimplemented("caml_dynlink_lookup_symbol");
        primitiveRegistry.unimplemented("caml_dynlink_open_lib");
        primitiveRegistry.addFunc1("caml_ensure_stack_capacity", (Value value) -> valTrue);
        primitiveRegistry.unimplemented("caml_ephe_blit_data");
        primitiveRegistry.unimplemented("caml_ephe_blit_key");
        primitiveRegistry.unimplemented("caml_ephe_check_data");
        primitiveRegistry.unimplemented("caml_ephe_check_key");
        primitiveRegistry.unimplemented("caml_ephe_create");
        primitiveRegistry.unimplemented("caml_ephe_get_data");
        primitiveRegistry.unimplemented("caml_ephe_get_data_copy");
        primitiveRegistry.unimplemented("caml_ephe_get_key");
        primitiveRegistry.unimplemented("caml_ephe_get_key_copy");
        primitiveRegistry.unimplemented("caml_ephe_set_data");
        primitiveRegistry.addFunc3("caml_ephe_set_key", Weak::setValue);
        primitiveRegistry.unimplemented("caml_ephe_unset_data");
        primitiveRegistry.addFunc2("caml_ephe_unset_key", Weak::unsetValue);
        primitiveRegistry.unimplemented("caml_eq_float");
        primitiveRegistry.addFunc2("caml_equal", compare::equal);
        primitiveRegistry.unimplemented("caml_eventlog_pause");
        primitiveRegistry.unimplemented("caml_eventlog_resume");
        primitiveRegistry.unimplemented("caml_exp_float");
        primitiveRegistry.unimplemented("caml_expm1_float");
        primitiveRegistry.addFuncN("caml_fill_bytes", (Value[] values) -> StringValue.fillBytes(
                (StringValue) values[0],
                (LongValue) values[1],
                (LongValue) values[2],
                (LongValue) values[3]
        ));
        primitiveRegistry.unimplemented("caml_fill_string");
        primitiveRegistry.unimplemented("caml_final_register");
        primitiveRegistry.unimplemented("caml_final_register_called_without_value");
        primitiveRegistry.unimplemented("caml_final_release");
        primitiveRegistry.unimplemented("caml_float_compare");
        primitiveRegistry.addFunc1("caml_float_of_int", DoubleValue::ofLongValue);
        primitiveRegistry.unimplemented("caml_float_of_string");
        primitiveRegistry.unimplemented("caml_floatarray_create");
        primitiveRegistry.addFunc2("caml_floatarray_get", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc3("caml_floatarray_set", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc2("caml_floatarray_unsafe_get", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc3("caml_floatarray_unsafe_set", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc1("caml_floor_float", DoubleValue::floor);
        primitiveRegistry.addFunc3("caml_fma_float", DoubleValue::fma);
        primitiveRegistry.addFunc2("caml_fmod_float", DoubleValue::fmod);
        primitiveRegistry.addFunc2("caml_format_float", DoubleValue::format);
        primitiveRegistry.addFunc2("caml_format_int", LongValue::format);
        primitiveRegistry.addPrimitive(new FreshOOIdPrimitive(ooIdGenerator));
        primitiveRegistry.unimplemented("caml_frexp_float");
        primitiveRegistry.unimplemented("caml_gc_compaction");
        primitiveRegistry.unimplemented("caml_gc_counters");
        primitiveRegistry.unimplemented("caml_gc_full_major");
        primitiveRegistry.unimplemented("caml_gc_get");
        primitiveRegistry.unimplemented("caml_gc_huge_fallback_count");
        primitiveRegistry.unimplemented("caml_gc_major");
        primitiveRegistry.unimplemented("caml_gc_major_slice");
        primitiveRegistry.unimplemented("caml_gc_minor");
        primitiveRegistry.unimplemented("caml_gc_minor_words");
        primitiveRegistry.unimplemented("caml_gc_quick_stat");
        primitiveRegistry.unimplemented("caml_gc_set");
        primitiveRegistry.unimplemented("caml_gc_stat");
        primitiveRegistry.unimplemented("caml_ge_float");
        primitiveRegistry.unimplemented("caml_get_current_callstack");
        primitiveRegistry.unimplemented("caml_get_current_environment");
        primitiveRegistry.unimplemented("caml_get_exception_backtrace");
        primitiveRegistry.addFunc0Ctx("caml_get_exception_raw_backtrace", backtrace::getExceptionRawBacktrace);
        primitiveRegistry.unimplemented("caml_get_global_data");
        primitiveRegistry.unimplemented("caml_get_major_bucket");
        primitiveRegistry.unimplemented("caml_get_major_credit");
        primitiveRegistry.unimplemented("caml_get_minor_free");
        primitiveRegistry.unimplemented("caml_get_public_method");
        primitiveRegistry.unimplemented("caml_get_section_table");
        primitiveRegistry.addFunc2("caml_greaterequal", compare::greaterEqual);
        primitiveRegistry.addFunc2("caml_greaterthan", compare::greaterThan);
        primitiveRegistry.unimplemented("caml_gt_float");
        primitiveRegistry.addFuncN("caml_hash", (Value[] values) -> Hash.camlHash(
                (LongValue) values[0],
                (LongValue) values[1],
                (LongValue) values[2],
                values[3]
        ));
        primitiveRegistry.unimplemented("caml_hash_univ_param");
        primitiveRegistry.unimplemented("caml_hexstring_of_float");
        primitiveRegistry.unimplemented("caml_hypot_float");
        primitiveRegistry.unimplemented("caml_input_value");
        primitiveRegistry.unimplemented("caml_input_value_from_bytes");
        primitiveRegistry.unimplemented("caml_input_value_from_string");
        primitiveRegistry.unimplemented("caml_input_value_to_outside_heap");
        primitiveRegistry.unimplemented("caml_install_signal_handler");
        primitiveRegistry.unimplemented("caml_int32_add");
        primitiveRegistry.unimplemented("caml_int32_and");
        primitiveRegistry.unimplemented("caml_int32_bits_of_float");
        primitiveRegistry.unimplemented("caml_int32_bswap");
        primitiveRegistry.unimplemented("caml_int32_compare");
        primitiveRegistry.unimplemented("caml_int32_div");
        primitiveRegistry.unimplemented("caml_int32_float_of_bits");
        primitiveRegistry.unimplemented("caml_int32_format");
        primitiveRegistry.unimplemented("caml_int32_mod");
        primitiveRegistry.unimplemented("caml_int32_mul");
        primitiveRegistry.unimplemented("caml_int32_neg");
        primitiveRegistry.unimplemented("caml_int32_of_float");
        primitiveRegistry.unimplemented("caml_int32_of_int");
        primitiveRegistry.unimplemented("caml_int32_of_string");
        primitiveRegistry.unimplemented("caml_int32_or");
        primitiveRegistry.unimplemented("caml_int32_shift_left");
        primitiveRegistry.unimplemented("caml_int32_shift_right");
        primitiveRegistry.unimplemented("caml_int32_shift_right_unsigned");
        primitiveRegistry.unimplemented("caml_int32_sub");
        primitiveRegistry.unimplemented("caml_int32_to_float");
        primitiveRegistry.unimplemented("caml_int32_to_int");
        primitiveRegistry.unimplemented("caml_int32_xor");
        primitiveRegistry.addFunc2("caml_int64_add", Int64CustomOperations::add);
        primitiveRegistry.unimplemented("caml_int64_add_native");
        primitiveRegistry.unimplemented("caml_int64_and");
        primitiveRegistry.unimplemented("caml_int64_and_native");
        primitiveRegistry.unimplemented("caml_int64_bits_of_float");
        primitiveRegistry.unimplemented("caml_int64_bswap");
        primitiveRegistry.addFunc2("caml_int64_compare", Int64CustomOperations::compare);
        primitiveRegistry.addFunc2("caml_int64_div", Int64CustomOperations::div);
        primitiveRegistry.unimplemented("caml_int64_div_native");
        primitiveRegistry.addPrimitive(new Int64FloatOfBitsPrimitive());
        primitiveRegistry.addFunc2("caml_int64_format", Int64CustomOperations::format);
        primitiveRegistry.addFunc2("caml_int64_mod", Int64CustomOperations::mod);
        primitiveRegistry.unimplemented("caml_int64_mod_native");
        primitiveRegistry.unimplemented("caml_int64_mul");
        primitiveRegistry.unimplemented("caml_int64_mul_native");
        primitiveRegistry.addFunc1("caml_int64_neg", Int64CustomOperations::neg);
        primitiveRegistry.unimplemented("caml_int64_neg_native");
        primitiveRegistry.addFunc1("caml_int64_of_float", Int64CustomOperations::ofFloat);
        primitiveRegistry.addFunc1("caml_int64_of_int", Int64CustomOperations::ofInt);
        primitiveRegistry.addFunc1("caml_int64_of_int32", Int64CustomOperations::ofInt);
        primitiveRegistry.unimplemented("caml_int64_of_nativeint");
        primitiveRegistry.addFunc1("caml_int64_of_string", Int64CustomOperations::ofString);
        primitiveRegistry.addFunc2("caml_int64_or", Int64CustomOperations::or);
        primitiveRegistry.unimplemented("caml_int64_or_native");
        primitiveRegistry.addFunc2("caml_int64_shift_left", Int64CustomOperations::shift_left);
        primitiveRegistry.addFunc2("caml_int64_shift_right", Int64CustomOperations::shift_right);
        primitiveRegistry.addFunc2("caml_int64_shift_right_unsigned", Int64CustomOperations::shift_right_unsigned);
        primitiveRegistry.addFunc2("caml_int64_sub", Int64CustomOperations::sub);
        primitiveRegistry.unimplemented("caml_int64_sub_native");
        primitiveRegistry.unimplemented("caml_int64_to_float");
        primitiveRegistry.unimplemented("caml_int64_to_int");
        primitiveRegistry.unimplemented("caml_int64_to_int32");
        primitiveRegistry.unimplemented("caml_int64_to_nativeint");
        primitiveRegistry.unimplemented("caml_int64_xor");
        primitiveRegistry.unimplemented("caml_int64_xor_native");
        primitiveRegistry.unimplemented("caml_int_as_pointer");
        primitiveRegistry.addFunc2("caml_int_compare", LongValue::compare);
        primitiveRegistry.addFunc1("caml_int_of_float", LongValue::ofDoubleValue);
        primitiveRegistry.addFunc1("caml_int_of_string", LongValue::parseString);
        primitiveRegistry.unimplemented("caml_invoke_traced_function");
        primitiveRegistry.unimplemented("caml_lazy_follow_forward");
        primitiveRegistry.unimplemented("caml_lazy_make_forward");
        primitiveRegistry.unimplemented("caml_ldexp_float");
        primitiveRegistry.unimplemented("caml_le_float");
        primitiveRegistry.addFunc2("caml_lessequal", compare::lessEqual);
        primitiveRegistry.addFunc2("caml_lessthan", compare::lessThan);
        primitiveRegistry.addFunc3("caml_lex_engine", Lexer::engine);
        primitiveRegistry.unimplemented("caml_log10_float");
        primitiveRegistry.unimplemented("caml_log1p_float");
        primitiveRegistry.unimplemented("caml_log_float");
        primitiveRegistry.unimplemented("caml_lt_float");
        primitiveRegistry.unimplemented("caml_make_array");
        primitiveRegistry.addFunc1("caml_make_float_vect", DoubleArray::makeVect);
        primitiveRegistry.addFunc2("caml_make_vect", BaseArrayValue::makeVect);
        primitiveRegistry.unimplemented("caml_marshal_data_size");
        primitiveRegistry.unimplemented("caml_md5_chan");
        primitiveRegistry.unimplemented("caml_md5_string");
        primitiveRegistry.unimplemented("caml_memprof_start");
        primitiveRegistry.unimplemented("caml_memprof_stop");
        primitiveRegistry.addFunc1("caml_ml_bytes_length", StringValue::stringLength);
        primitiveRegistry.unimplemented("caml_ml_channel_size");
        primitiveRegistry.unimplemented("caml_ml_channel_size_64");
        primitiveRegistry.unimplemented("caml_ml_close_channel");
        primitiveRegistry.unimplemented("caml_ml_enable_runtime_warnings");
        primitiveRegistry.addPrimitive(new MlFlush());
        primitiveRegistry.unimplemented("caml_ml_flush_partial");
        primitiveRegistry.unimplemented("caml_ml_input");
        primitiveRegistry.unimplemented("caml_ml_input_char");
        primitiveRegistry.unimplemented("caml_ml_input_int");
        primitiveRegistry.unimplemented("caml_ml_input_scan_line");
        primitiveRegistry.addPrimitive(new MlOpenDescriptorIn(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOpenDescriptorOut(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOutChannelsList(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOutput());
        primitiveRegistry.unimplemented("caml_ml_output_bytes");
        primitiveRegistry.addPrimitive(new MlOutputCharPrimitive());
        primitiveRegistry.unimplemented("caml_ml_output_int");
        primitiveRegistry.unimplemented("caml_ml_output_partial");
        primitiveRegistry.unimplemented("caml_ml_pos_in");
        primitiveRegistry.unimplemented("caml_ml_pos_in_64");
        primitiveRegistry.unimplemented("caml_ml_pos_out");
        primitiveRegistry.unimplemented("caml_ml_pos_out_64");
        primitiveRegistry.unimplemented("caml_ml_runtime_warnings_enabled");
        primitiveRegistry.unimplemented("caml_ml_seek_in");
        primitiveRegistry.unimplemented("caml_ml_seek_in_64");
        primitiveRegistry.unimplemented("caml_ml_seek_out");
        primitiveRegistry.unimplemented("caml_ml_seek_out_64");
        primitiveRegistry.unimplemented("caml_ml_set_binary_mode");
        primitiveRegistry.unimplemented("caml_ml_set_channel_name");
        primitiveRegistry.addPrimitive(new MlStringLength());
        primitiveRegistry.unimplemented("caml_modf_float");
        primitiveRegistry.addFunc2("caml_mul_float", DoubleValue::mul);
        primitiveRegistry.addFunc2("caml_nativeint_add", NativeIntCustomOperations::add);
        primitiveRegistry.addFunc2("caml_nativeint_and", NativeIntCustomOperations::and);
        primitiveRegistry.unimplemented("caml_nativeint_bswap");
        primitiveRegistry.addFunc2("caml_nativeint_compare", NativeIntCustomOperations::compare);
        primitiveRegistry.addFunc2("caml_nativeint_div", NativeIntCustomOperations::div);
        primitiveRegistry.addFunc2("caml_nativeint_format", NativeIntCustomOperations::format);
        primitiveRegistry.addFunc2("caml_nativeint_mod", NativeIntCustomOperations::mod);
        primitiveRegistry.addFunc2("caml_nativeint_mul", NativeIntCustomOperations::mul);
        primitiveRegistry.addFunc1("caml_nativeint_neg", NativeIntCustomOperations::neg);
        primitiveRegistry.addFunc1("caml_nativeint_of_float", NativeIntCustomOperations::ofFloat);
        primitiveRegistry.addFunc1("caml_nativeint_of_int", NativeIntCustomOperations::ofInt);
        primitiveRegistry.unimplemented("caml_nativeint_of_int32");
        primitiveRegistry.addFunc1("caml_nativeint_of_string", NativeIntCustomOperations::ofString);
        primitiveRegistry.addFunc2("caml_nativeint_or", NativeIntCustomOperations::or);

        primitiveRegistry.addFunc2("caml_nativeint_shift_left", NativeIntCustomOperations::shift_left);
        primitiveRegistry.addFunc2("caml_nativeint_shift_right", NativeIntCustomOperations::shift_right);
        primitiveRegistry.addFunc2("caml_nativeint_shift_right_unsigned", NativeIntCustomOperations::shift_right_unsigned);
        primitiveRegistry.addFunc2("caml_nativeint_sub", NativeIntCustomOperations::sub);
        primitiveRegistry.unimplemented("caml_nativeint_to_float");
        primitiveRegistry.unimplemented("caml_nativeint_to_int");
        primitiveRegistry.unimplemented("caml_nativeint_to_int32");
        primitiveRegistry.addFunc2("caml_nativeint_xor", NativeIntCustomOperations::xor);
        primitiveRegistry.addFunc1("caml_neg_float", DoubleValue::neg);
        primitiveRegistry.unimplemented("caml_neq_float");
        primitiveRegistry.addFunc3("caml_new_lex_engine", Lexer::caml_new_lex_engine);
        primitiveRegistry.unimplemented("caml_nextafter_float");
        primitiveRegistry.unimplemented("caml_notequal");
        primitiveRegistry.unimplemented("caml_obj_add_offset");
        primitiveRegistry.addFunc2("caml_obj_block", Obj::objBlock);
        primitiveRegistry.addFunc1("caml_obj_dup", BaseArrayValue::duplicateArray);
        primitiveRegistry.unimplemented("caml_obj_is_block");
        primitiveRegistry.unimplemented("caml_obj_make_forward");
        primitiveRegistry.unimplemented("caml_obj_reachable_words");
        primitiveRegistry.unimplemented("caml_obj_set_tag");
        primitiveRegistry.unimplemented("caml_obj_tag");
        primitiveRegistry.unimplemented("caml_obj_truncate");
        primitiveRegistry.unimplemented("caml_obj_with_tag");
        primitiveRegistry.unimplemented("caml_output_value");
        primitiveRegistry.unimplemented("caml_output_value_to_buffer");
        primitiveRegistry.unimplemented("caml_output_value_to_bytes");
        primitiveRegistry.unimplemented("caml_output_value_to_string");
        primitiveRegistry.unimplemented("caml_parse_engine");
        primitiveRegistry.unimplemented("caml_power_float");
        primitiveRegistry.unimplemented("caml_raw_backtrace_length");
        primitiveRegistry.unimplemented("caml_raw_backtrace_next_slot");
        primitiveRegistry.addFunc2("caml_raw_backtrace_slot", backtrace::rawBacktraceSlot);
        primitiveRegistry.unimplemented("caml_realloc_global");
        primitiveRegistry.addFunc1("caml_record_backtrace", backtrace::recordBacktrace);
        primitiveRegistry.unimplemented("caml_register_channel_for_spacetime");
        primitiveRegistry.addPrimitive(new RegisterNamedValuePrimitive(namedValues));
        primitiveRegistry.unimplemented("caml_reify_bytecode");
        primitiveRegistry.unimplemented("caml_remove_debug_info");
        primitiveRegistry.unimplemented("caml_reset_afl_instrumentation");
        primitiveRegistry.unimplemented("caml_restore_raw_backtrace");
        primitiveRegistry.unimplemented("caml_round_float");
        primitiveRegistry.unimplemented("caml_runtime_parameters");
        primitiveRegistry.unimplemented("caml_runtime_variant");
        primitiveRegistry.addFunc1("caml_set_oo_id", ooIdGenerator::setOOId);
        primitiveRegistry.unimplemented("caml_set_parser_trace");
        primitiveRegistry.unimplemented("caml_setup_afl");
        primitiveRegistry.unimplemented("caml_signbit");
        primitiveRegistry.unimplemented("caml_signbit_float");
        primitiveRegistry.addFunc1("caml_sin_float", DoubleValue::sin);
        primitiveRegistry.addFunc1("caml_sinh_float", DoubleValue::sinh);
        primitiveRegistry.unimplemented("caml_spacetime_enabled");
        primitiveRegistry.unimplemented("caml_spacetime_only_works_for_native_code");
        primitiveRegistry.unimplemented("caml_sqrt_float");
        primitiveRegistry.unimplemented("caml_static_alloc");
        primitiveRegistry.unimplemented("caml_static_free");
        primitiveRegistry.unimplemented("caml_static_release_bytecode");
        primitiveRegistry.unimplemented("caml_static_resize");
        primitiveRegistry.addFunc2("caml_string_compare", StringValue::compare);
        primitiveRegistry.addFunc2("caml_string_equal", StringValue::equal);
        primitiveRegistry.addFunc2("caml_string_get", StringValue::getByteValue);
        primitiveRegistry.unimplemented("caml_string_get16");
        primitiveRegistry.unimplemented("caml_string_get32");
        primitiveRegistry.unimplemented("caml_string_get64");
        primitiveRegistry.unimplemented("caml_string_greaterequal");
        primitiveRegistry.unimplemented("caml_string_greaterthan");
        primitiveRegistry.unimplemented("caml_string_lessequal");
        primitiveRegistry.unimplemented("caml_string_lessthan");
        primitiveRegistry.addFunc2("caml_string_notequal", StringValue::notEqual);
        primitiveRegistry.addFunc1("caml_string_of_bytes", Value::identity);
        primitiveRegistry.unimplemented("caml_string_set");
        primitiveRegistry.unimplemented("caml_sub_float");
        primitiveRegistry.addFunc1("caml_sys_argv", sys::argv);
        primitiveRegistry.addFunc1("caml_sys_chdir", sys::chDir);
        primitiveRegistry.unimplemented("caml_sys_close");
        primitiveRegistry.addPrimitive(new SysConstBackendType());
        primitiveRegistry.addPrimitive(new SysConstBigEndian());
        primitiveRegistry.addFunc0("caml_sys_const_int_size", sys::constIntSize);;
        primitiveRegistry.addFunc0("caml_sys_const_max_wosize", sys::constOsMaxWoSize);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_cygwin", sys::constOsTypeCygwin);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_unix", sys::constOsTypeUnix);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_win32", sys::constOsTypeWin32);
        primitiveRegistry.addFunc0("caml_sys_const_word_size", sys::sysConstWordSize);
        primitiveRegistry.addPrimitive(new SysExecutableName());
        primitiveRegistry.addFunc1("caml_sys_exit", sys::exit);
        primitiveRegistry.addFunc1("caml_sys_file_exists", sys::fileExists);
        primitiveRegistry.unimplemented("caml_sys_get_argv");
        primitiveRegistry.addPrimitive(new SysGetConfig());
        primitiveRegistry.addFunc1("caml_sys_getcwd", sys::getCwd);
        primitiveRegistry.addFunc1("caml_sys_getenv", sys::getEnv);
        primitiveRegistry.addFunc1("caml_sys_is_directory", sys::isDirectory);
        primitiveRegistry.unimplemented("caml_sys_isatty");
        primitiveRegistry.unimplemented("caml_sys_modify_argv");
        primitiveRegistry.unimplemented("caml_sys_open");
        primitiveRegistry.unimplemented("caml_sys_random_seed");
        primitiveRegistry.addFunc1("caml_sys_read_directory", sys::readDirectory);
        primitiveRegistry.unimplemented("caml_sys_remove");
        primitiveRegistry.unimplemented("caml_sys_rename");
        primitiveRegistry.unimplemented("caml_sys_system_command");
        primitiveRegistry.unimplemented("caml_sys_time");
        primitiveRegistry.unimplemented("caml_sys_time_include_children");
        primitiveRegistry.unimplemented("caml_sys_unsafe_getenv");
        primitiveRegistry.addFunc1("caml_tan_float", DoubleValue::tan);
        primitiveRegistry.addFunc1("caml_tanh_float", DoubleValue::tanh);
        primitiveRegistry.unimplemented("caml_terminfo_rows");
        primitiveRegistry.unimplemented("caml_trunc_float");
        primitiveRegistry.addFunc2("caml_update_dummy", Dummy::update);
        primitiveRegistry.addFuncN("caml_weak_blit", (Value[] values) -> Weak.blit(
                (Weak) values[0],
                (LongValue) values[1],
                (Weak) values[2],
                (LongValue) values[3],
                (LongValue) values[4]
        ));
        primitiveRegistry.unimplemented("caml_weak_check");
        primitiveRegistry.addFunc1("caml_weak_create", Weak::create);
        primitiveRegistry.addFunc2("caml_weak_get", Weak::getValue);
        primitiveRegistry.unimplemented("caml_weak_get_copy");
        primitiveRegistry.unimplemented("caml_weak_set");


    }

    private static ObjectValue allocDummy(LongValue length) {
        return new ObjectValue(ValueTag.PAIR_TAG, LongValue.unwrapInt(length));
    }

    public void execute(Path executable) throws IOException {
        Path path = executable; //Path.of("/Users/sidharthkuruvila/CLionProjects/ocaml/ocamlc");
        FileChannel fc = FileChannel.open(path);
        Executable e = exb.fromExe(fc);
        Primitives primitives = primitiveRegistry.getPrimitives(e.getPrims());
        Interpreter interpreter = new Interpreter(e.getGlobalData(), primitives, camlState, e.getDebugEvents());
//            HexPrinter.printBytes(e.getCodeFragment().code);

        interpreter.interpret(e.getCodeFragment().code);
    }

}
