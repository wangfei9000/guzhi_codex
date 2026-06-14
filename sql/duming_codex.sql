--
-- PostgreSQL database dump
--

-- Dumped from database version 16.14 (Ubuntu 16.14-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 17.0

-- Started on 2026-06-14 14:22:30 CST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 235 (class 1259 OID 197775)
-- Name: collateral; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.collateral (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    collateral_code character varying(50) NOT NULL,
    collateral_type character varying(50),
    collateral_name character varying(200),
    collateral_address character varying(255),
    building_area numeric(14,2),
    land_area numeric(14,2),
    community_name character varying(100),
    building character varying(50),
    unit_name character varying(50),
    door_number character varying(50),
    build_year integer,
    construction_land numeric(14,2),
    land_acquisition numeric(14,2),
    floor_area_ratio numeric(10,4),
    above_ground_ratio numeric(10,4),
    civil_defense_area numeric(14,2),
    underground_ratio numeric(10,4),
    greening_rate numeric(10,4),
    building_density numeric(10,4),
    building_height numeric(10,2),
    floor_count integer,
    household_count integer,
    parking_count integer,
    parking_ratio numeric(10,4),
    completion_date date,
    property_rights_years integer,
    land_use_years integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_primary boolean,
    actual_use character varying(100),
    occupancy_status character varying(100),
    decoration character varying(100),
    orientation character varying(50),
    current_floor character varying(50),
    indoor_height character varying(50),
    space_layout text,
    facilities_condition text,
    maintenance_condition text,
    parcel_shape text,
    terrain text,
    land_level text,
    soil_condition text,
    land_development_level text,
    landscape text,
    surrounding_environment text
);


ALTER TABLE public.collateral OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 197774)
-- Name: collateral_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.collateral_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.collateral_id_seq OWNER TO postgres;

--
-- TOC entry 3681 (class 0 OID 0)
-- Dependencies: 234
-- Name: collateral_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.collateral_id_seq OWNED BY public.collateral.id;


--
-- TOC entry 262 (class 1259 OID 206387)
-- Name: d_city; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.d_city (
    id bigint,
    province_id bigint,
    code character varying(16),
    name character varying(16),
    parent_id integer,
    type integer
);


ALTER TABLE public.d_city OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 206390)
-- Name: d_district; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.d_district (
    id bigint,
    code character varying(16),
    name character varying(32),
    city_id bigint,
    parent_id integer
);


ALTER TABLE public.d_district OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 206396)
-- Name: d_prestreet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.d_prestreet (
    id bigint,
    code character varying(20),
    name character varying(50),
    city_id integer,
    type integer,
    district_id integer,
    parent_id integer
);


ALTER TABLE public.d_prestreet OWNER TO postgres;

--
-- TOC entry 264 (class 1259 OID 206393)
-- Name: d_province; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.d_province (
    id bigint,
    code character varying(16),
    name character varying(32),
    parent_id bigint
);


ALTER TABLE public.d_province OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 197631)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO dm;

--
-- TOC entry 243 (class 1259 OID 197873)
-- Name: ownership_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ownership_info (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    right_holder character varying(200),
    right_certificate_number character varying(200),
    borrower_name character varying(100),
    borrower_id_card character varying(50),
    building_structure character varying(100),
    usage character varying(200),
    current_floor character varying(50),
    total_floors integer,
    right_nature character varying(200),
    right_type character varying(200),
    co_ownership character varying(200),
    land_use_years integer,
    property_unit_number character varying(200),
    shared_land_area numeric(18,4),
    allocated_land_area numeric(18,4),
    build_year integer,
    build_year_source character varying(200),
    online_signing_date date,
    contract_number character varying(200),
    report_issue_date date,
    valuation_time_point date,
    old_community_renovation boolean,
    area_prosperity character varying(200),
    market_prosperity character varying(200),
    house_ownership_certificate character varying(200),
    state_land_use_certificate_number character varying(200),
    land_use character varying(200),
    qiu_quan_number character varying(200),
    land_use_area numeric(18,4),
    registered_address character varying(500),
    registered_building_area numeric(18,4),
    right_status character varying(100),
    right_registration_date date,
    right_cancellation_date date,
    property_source character varying(200),
    land_use_right_source character varying(200),
    land_use_start_date date,
    land_use_end_date date,
    mortgage_info text,
    seizure_info text,
    lease_restriction text,
    other_rights_info text,
    remark text,
    actual_use character varying(200),
    decoration character varying(100)
);


ALTER TABLE public.ownership_info OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 197872)
-- Name: ownership_info_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ownership_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ownership_info_id_seq OWNER TO postgres;

--
-- TOC entry 3682 (class 0 OID 0)
-- Dependencies: 242
-- Name: ownership_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ownership_info_id_seq OWNED BY public.ownership_info.id;


--
-- TOC entry 233 (class 1259 OID 197760)
-- Name: project; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.project (
    id bigint NOT NULL,
    project_code character varying(50) NOT NULL,
    project_name character varying(200) NOT NULL,
    city character varying(50),
    district character varying(50),
    area character varying(100),
    address character varying(255),
    registrar character varying(50),
    registration_date date,
    client_contact character varying(50),
    client_phone character varying(20),
    valuation_purpose character varying(100),
    valuation_time date,
    expected_price numeric(14,2),
    status character varying(20) DEFAULT '未评估'::character varying NOT NULL,
    remark text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valuation_unit_price numeric(12,2),
    valuation_total_price numeric(14,2),
    building_area numeric(14,2),
    client_name character varying(200),
    mortgagor_name character varying(200),
    mortgagor_id_card character varying(50),
    mortgagor_phone character varying(20),
    borrower_name character varying(200),
    borrower_id_card character varying(50),
    valuation_type character varying(20)
);


ALTER TABLE public.project OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 197759)
-- Name: project_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.project_id_seq OWNER TO postgres;

--
-- TOC entry 3683 (class 0 OID 0)
-- Dependencies: 232
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.project_id_seq OWNED BY public.project.id;


--
-- TOC entry 259 (class 1259 OID 206331)
-- Name: reconciliation_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reconciliation_record (
    id bigint NOT NULL,
    organization_id bigint,
    start_time date NOT NULL,
    end_time date NOT NULL,
    reconciliation_date date,
    result character varying(20) NOT NULL,
    file_url character varying(500),
    remark text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.reconciliation_record OWNER TO postgres;

--
-- TOC entry 258 (class 1259 OID 206330)
-- Name: reconciliation_record_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reconciliation_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reconciliation_record_id_seq OWNER TO postgres;

--
-- TOC entry 3684 (class 0 OID 0)
-- Dependencies: 258
-- Name: reconciliation_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reconciliation_record_id_seq OWNED BY public.reconciliation_record.id;


--
-- TOC entry 245 (class 1259 OID 197890)
-- Name: report_review; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.report_review (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    report_id bigint NOT NULL,
    reviewer character varying(50),
    review_date date,
    review_opinion text,
    review_result character varying(50),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.report_review OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 197889)
-- Name: report_review_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.report_review_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.report_review_id_seq OWNER TO postgres;

--
-- TOC entry 3685 (class 0 OID 0)
-- Dependencies: 244
-- Name: report_review_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.report_review_id_seq OWNED BY public.report_review.id;


--
-- TOC entry 249 (class 1259 OID 197936)
-- Name: report_seal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.report_seal (
    id bigint NOT NULL,
    report_id bigint NOT NULL,
    project_id bigint NOT NULL,
    sealed_report_url character varying(500),
    sealer character varying(50),
    seal_date date,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.report_seal OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 197935)
-- Name: report_seal_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.report_seal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.report_seal_id_seq OWNER TO postgres;

--
-- TOC entry 3686 (class 0 OID 0)
-- Dependencies: 248
-- Name: report_seal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.report_seal_id_seq OWNED BY public.report_seal.id;


--
-- TOC entry 261 (class 1259 OID 206350)
-- Name: report_template; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.report_template (
    id bigint NOT NULL,
    template_name character varying(100) NOT NULL,
    template_content text NOT NULL,
    status character varying(20) DEFAULT '启用'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.report_template OWNER TO postgres;

--
-- TOC entry 260 (class 1259 OID 206349)
-- Name: report_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.report_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.report_template_id_seq OWNER TO postgres;

--
-- TOC entry 3687 (class 0 OID 0)
-- Dependencies: 260
-- Name: report_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.report_template_id_seq OWNED BY public.report_template.id;


--
-- TOC entry 257 (class 1259 OID 206314)
-- Name: revaluation_project; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.revaluation_project (
    id bigint NOT NULL,
    revaluation_id bigint NOT NULL,
    project_code character varying(50) NOT NULL,
    unit_price numeric(12,2),
    total_price numeric(14,2),
    remark text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.revaluation_project OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 206313)
-- Name: revaluation_project_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.revaluation_project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.revaluation_project_id_seq OWNER TO postgres;

--
-- TOC entry 3688 (class 0 OID 0)
-- Dependencies: 256
-- Name: revaluation_project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.revaluation_project_id_seq OWNED BY public.revaluation_project.id;


--
-- TOC entry 255 (class 1259 OID 206302)
-- Name: revaluation_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.revaluation_record (
    id bigint NOT NULL,
    revaluation_date date,
    result character varying(20) NOT NULL,
    file_url character varying(500),
    remark text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    organization_id bigint
);


ALTER TABLE public.revaluation_record OWNER TO postgres;

--
-- TOC entry 254 (class 1259 OID 206301)
-- Name: revaluation_record_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.revaluation_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.revaluation_record_id_seq OWNER TO postgres;

--
-- TOC entry 3689 (class 0 OID 0)
-- Dependencies: 254
-- Name: revaluation_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.revaluation_record_id_seq OWNED BY public.revaluation_record.id;


--
-- TOC entry 241 (class 1259 OID 197832)
-- Name: survey; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.survey (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    survey_code character varying(50) NOT NULL,
    surveyor character varying(50),
    receptionist character varying(50),
    receptionist_phone character varying(20),
    survey_date date,
    start_time time without time zone,
    end_time time without time zone,
    property_cert_verified boolean,
    ownership_dispute text,
    remark text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    code character varying(4),
    survey_status character varying(20) DEFAULT '未查勘'::character varying NOT NULL
);


ALTER TABLE public.survey OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 197831)
-- Name: survey_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.survey_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.survey_id_seq OWNER TO postgres;

--
-- TOC entry 3690 (class 0 OID 0)
-- Dependencies: 240
-- Name: survey_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.survey_id_seq OWNED BY public.survey.id;


--
-- TOC entry 247 (class 1259 OID 197913)
-- Name: survey_photo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.survey_photo (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    survey_id bigint NOT NULL,
    photo_code character varying(50) NOT NULL,
    photo_path character varying(500),
    photo_description text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    photo_category character varying(50)
);


ALTER TABLE public.survey_photo OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 197912)
-- Name: survey_photo_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.survey_photo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.survey_photo_id_seq OWNER TO postgres;

--
-- TOC entry 3691 (class 0 OID 0)
-- Dependencies: 246
-- Name: survey_photo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.survey_photo_id_seq OWNED BY public.survey_photo.id;


--
-- TOC entry 229 (class 1259 OID 197733)
-- Name: sys_file_record; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_file_record (
    id bigint NOT NULL,
    original_name character varying(255) NOT NULL,
    stored_name character varying(255) NOT NULL,
    file_path character varying(500) NOT NULL,
    file_size bigint NOT NULL,
    content_type character varying(100),
    upload_user_id bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.sys_file_record OWNER TO dm;

--
-- TOC entry 228 (class 1259 OID 197732)
-- Name: sys_file_record_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_file_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_file_record_id_seq OWNER TO dm;

--
-- TOC entry 3692 (class 0 OID 0)
-- Dependencies: 228
-- Name: sys_file_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_file_record_id_seq OWNED BY public.sys_file_record.id;


--
-- TOC entry 227 (class 1259 OID 197720)
-- Name: sys_notification; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_notification (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    title character varying(200) NOT NULL,
    content text NOT NULL,
    is_read boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    sender_id bigint
);


ALTER TABLE public.sys_notification OWNER TO dm;

--
-- TOC entry 226 (class 1259 OID 197719)
-- Name: sys_notification_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_notification_id_seq OWNER TO dm;

--
-- TOC entry 3693 (class 0 OID 0)
-- Dependencies: 226
-- Name: sys_notification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_notification_id_seq OWNED BY public.sys_notification.id;


--
-- TOC entry 253 (class 1259 OID 206285)
-- Name: sys_organization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sys_organization (
    id bigint NOT NULL,
    organization_type character varying(100),
    organization_name character varying(200) NOT NULL,
    contact_name character varying(100),
    contact_phone character varying(30),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    report_template_id bigint
);


ALTER TABLE public.sys_organization OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 206284)
-- Name: sys_organization_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sys_organization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_organization_id_seq OWNER TO postgres;

--
-- TOC entry 3694 (class 0 OID 0)
-- Dependencies: 252
-- Name: sys_organization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sys_organization_id_seq OWNED BY public.sys_organization.id;


--
-- TOC entry 221 (class 1259 OID 197669)
-- Name: sys_permission; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_permission (
    id bigint NOT NULL,
    perm_name character varying(50) NOT NULL,
    perm_code character varying(100) NOT NULL,
    parent_id bigint,
    type character varying(20) NOT NULL,
    path character varying(200),
    icon character varying(50),
    sort_order integer DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.sys_permission OWNER TO dm;

--
-- TOC entry 220 (class 1259 OID 197668)
-- Name: sys_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_permission_id_seq OWNER TO dm;

--
-- TOC entry 3695 (class 0 OID 0)
-- Dependencies: 220
-- Name: sys_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_permission_id_seq OWNED BY public.sys_permission.id;


--
-- TOC entry 219 (class 1259 OID 197655)
-- Name: sys_role; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_role (
    id bigint NOT NULL,
    role_name character varying(50) NOT NULL,
    role_code character varying(50) NOT NULL,
    description character varying(200),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.sys_role OWNER TO dm;

--
-- TOC entry 218 (class 1259 OID 197654)
-- Name: sys_role_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_role_id_seq OWNER TO dm;

--
-- TOC entry 3696 (class 0 OID 0)
-- Dependencies: 218
-- Name: sys_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_role_id_seq OWNED BY public.sys_role.id;


--
-- TOC entry 225 (class 1259 OID 197701)
-- Name: sys_role_permission; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_role_permission (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


ALTER TABLE public.sys_role_permission OWNER TO dm;

--
-- TOC entry 224 (class 1259 OID 197700)
-- Name: sys_role_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_role_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_role_permission_id_seq OWNER TO dm;

--
-- TOC entry 3697 (class 0 OID 0)
-- Dependencies: 224
-- Name: sys_role_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_role_permission_id_seq OWNED BY public.sys_role_permission.id;


--
-- TOC entry 230 (class 1259 OID 197746)
-- Name: sys_schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sys_schedule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_schedule_id_seq OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 197642)
-- Name: sys_user; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_user (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(100),
    phone character varying(20),
    nickname character varying(50),
    status integer DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    organization_id bigint
);


ALTER TABLE public.sys_user OWNER TO dm;

--
-- TOC entry 216 (class 1259 OID 197641)
-- Name: sys_user_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_user_id_seq OWNER TO dm;

--
-- TOC entry 3698 (class 0 OID 0)
-- Dependencies: 216
-- Name: sys_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_user_id_seq OWNED BY public.sys_user.id;


--
-- TOC entry 223 (class 1259 OID 197682)
-- Name: sys_user_role; Type: TABLE; Schema: public; Owner: dm
--

CREATE TABLE public.sys_user_role (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.sys_user_role OWNER TO dm;

--
-- TOC entry 222 (class 1259 OID 197681)
-- Name: sys_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: dm
--

CREATE SEQUENCE public.sys_user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sys_user_role_id_seq OWNER TO dm;

--
-- TOC entry 3699 (class 0 OID 0)
-- Dependencies: 222
-- Name: sys_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dm
--

ALTER SEQUENCE public.sys_user_role_id_seq OWNED BY public.sys_user_role.id;


--
-- TOC entry 239 (class 1259 OID 197813)
-- Name: valuation_method; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.valuation_method (
    id bigint NOT NULL,
    method_code character varying(50) NOT NULL,
    method_name character varying(100) NOT NULL,
    weight numeric(5,4),
    unit_price numeric(12,2),
    appraiser_signature character varying(50),
    description text,
    report_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.valuation_method OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 197812)
-- Name: valuation_method_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.valuation_method_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.valuation_method_id_seq OWNER TO postgres;

--
-- TOC entry 3700 (class 0 OID 0)
-- Dependencies: 238
-- Name: valuation_method_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.valuation_method_id_seq OWNED BY public.valuation_method.id;


--
-- TOC entry 251 (class 1259 OID 206270)
-- Name: valuation_price; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.valuation_price (
    id bigint NOT NULL,
    city character varying(50),
    district character varying(50),
    address character varying(500),
    unit_price numeric(12,2),
    total_price numeric(14,2),
    area numeric(14,2),
    valuation_time date,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.valuation_price OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 206269)
-- Name: valuation_price_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.valuation_price_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.valuation_price_id_seq OWNER TO postgres;

--
-- TOC entry 3701 (class 0 OID 0)
-- Dependencies: 250
-- Name: valuation_price_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.valuation_price_id_seq OWNED BY public.valuation_price.id;


--
-- TOC entry 237 (class 1259 OID 197794)
-- Name: valuation_report; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.valuation_report (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    report_code character varying(50) NOT NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    unit_price numeric(12,2),
    valuation_result text,
    area_evaluation text,
    surrounding_transactions text,
    liquidity_analysis text,
    floor_plan text,
    land_grant_deduction numeric(14,2),
    decoration_new_rate numeric(5,4),
    equipment_new_rate numeric(5,4),
    report_url character varying(500),
    bank_suggestion text,
    land_plot text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_price numeric(14,2),
    mortgage_value numeric(14,2),
    priority_compensation_amount numeric(14,2),
    priority_compensation_description text,
    value_date date,
    report_issue_date date,
    valid_start_date date,
    valid_end_date date,
    valuer1_name character varying(50),
    valuer1_cert_no character varying(50),
    valuer2_name character varying(50),
    valuer2_cert_no character varying(50)
);


ALTER TABLE public.valuation_report OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 197793)
-- Name: valuation_report_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.valuation_report_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.valuation_report_id_seq OWNER TO postgres;

--
-- TOC entry 3702 (class 0 OID 0)
-- Dependencies: 236
-- Name: valuation_report_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.valuation_report_id_seq OWNED BY public.valuation_report.id;


--
-- TOC entry 3377 (class 2604 OID 197778)
-- Name: collateral id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.collateral ALTER COLUMN id SET DEFAULT nextval('public.collateral_id_seq'::regclass);


--
-- TOC entry 3390 (class 2604 OID 197876)
-- Name: ownership_info id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ownership_info ALTER COLUMN id SET DEFAULT nextval('public.ownership_info_id_seq'::regclass);


--
-- TOC entry 3373 (class 2604 OID 197763)
-- Name: project id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project ALTER COLUMN id SET DEFAULT nextval('public.project_id_seq'::regclass);


--
-- TOC entry 3414 (class 2604 OID 206334)
-- Name: reconciliation_record id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reconciliation_record ALTER COLUMN id SET DEFAULT nextval('public.reconciliation_record_id_seq'::regclass);


--
-- TOC entry 3393 (class 2604 OID 197893)
-- Name: report_review id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_review ALTER COLUMN id SET DEFAULT nextval('public.report_review_id_seq'::regclass);


--
-- TOC entry 3399 (class 2604 OID 197939)
-- Name: report_seal id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_seal ALTER COLUMN id SET DEFAULT nextval('public.report_seal_id_seq'::regclass);


--
-- TOC entry 3417 (class 2604 OID 206353)
-- Name: report_template id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_template ALTER COLUMN id SET DEFAULT nextval('public.report_template_id_seq'::regclass);


--
-- TOC entry 3411 (class 2604 OID 206317)
-- Name: revaluation_project id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revaluation_project ALTER COLUMN id SET DEFAULT nextval('public.revaluation_project_id_seq'::regclass);


--
-- TOC entry 3408 (class 2604 OID 206305)
-- Name: revaluation_record id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revaluation_record ALTER COLUMN id SET DEFAULT nextval('public.revaluation_record_id_seq'::regclass);


--
-- TOC entry 3386 (class 2604 OID 197835)
-- Name: survey id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey ALTER COLUMN id SET DEFAULT nextval('public.survey_id_seq'::regclass);


--
-- TOC entry 3396 (class 2604 OID 197916)
-- Name: survey_photo id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey_photo ALTER COLUMN id SET DEFAULT nextval('public.survey_photo_id_seq'::regclass);


--
-- TOC entry 3370 (class 2604 OID 197736)
-- Name: sys_file_record id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_file_record ALTER COLUMN id SET DEFAULT nextval('public.sys_file_record_id_seq'::regclass);


--
-- TOC entry 3366 (class 2604 OID 197723)
-- Name: sys_notification id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_notification ALTER COLUMN id SET DEFAULT nextval('public.sys_notification_id_seq'::regclass);


--
-- TOC entry 3405 (class 2604 OID 206288)
-- Name: sys_organization id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sys_organization ALTER COLUMN id SET DEFAULT nextval('public.sys_organization_id_seq'::regclass);


--
-- TOC entry 3360 (class 2604 OID 197672)
-- Name: sys_permission id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_permission ALTER COLUMN id SET DEFAULT nextval('public.sys_permission_id_seq'::regclass);


--
-- TOC entry 3357 (class 2604 OID 197658)
-- Name: sys_role id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role ALTER COLUMN id SET DEFAULT nextval('public.sys_role_id_seq'::regclass);


--
-- TOC entry 3365 (class 2604 OID 197704)
-- Name: sys_role_permission id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role_permission ALTER COLUMN id SET DEFAULT nextval('public.sys_role_permission_id_seq'::regclass);


--
-- TOC entry 3353 (class 2604 OID 197645)
-- Name: sys_user id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user ALTER COLUMN id SET DEFAULT nextval('public.sys_user_id_seq'::regclass);


--
-- TOC entry 3364 (class 2604 OID 197685)
-- Name: sys_user_role id; Type: DEFAULT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user_role ALTER COLUMN id SET DEFAULT nextval('public.sys_user_role_id_seq'::regclass);


--
-- TOC entry 3383 (class 2604 OID 197816)
-- Name: valuation_method id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_method ALTER COLUMN id SET DEFAULT nextval('public.valuation_method_id_seq'::regclass);


--
-- TOC entry 3402 (class 2604 OID 206273)
-- Name: valuation_price id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_price ALTER COLUMN id SET DEFAULT nextval('public.valuation_price_id_seq'::regclass);


--
-- TOC entry 3380 (class 2604 OID 197797)
-- Name: valuation_report id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_report ALTER COLUMN id SET DEFAULT nextval('public.valuation_report_id_seq'::regclass);


--
-- TOC entry 3457 (class 2606 OID 197786)
-- Name: collateral collateral_collateral_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.collateral
    ADD CONSTRAINT collateral_collateral_code_key UNIQUE (collateral_code);


--
-- TOC entry 3459 (class 2606 OID 197784)
-- Name: collateral collateral_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.collateral
    ADD CONSTRAINT collateral_pkey PRIMARY KEY (id);


--
-- TOC entry 3422 (class 2606 OID 197638)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 3473 (class 2606 OID 197882)
-- Name: ownership_info ownership_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ownership_info
    ADD CONSTRAINT ownership_info_pkey PRIMARY KEY (id);


--
-- TOC entry 3453 (class 2606 OID 197770)
-- Name: project project_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- TOC entry 3455 (class 2606 OID 197772)
-- Name: project project_project_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_project_code_key UNIQUE (project_code);


--
-- TOC entry 3494 (class 2606 OID 206340)
-- Name: reconciliation_record reconciliation_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reconciliation_record
    ADD CONSTRAINT reconciliation_record_pkey PRIMARY KEY (id);


--
-- TOC entry 3475 (class 2606 OID 197899)
-- Name: report_review report_review_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_review
    ADD CONSTRAINT report_review_pkey PRIMARY KEY (id);


--
-- TOC entry 3482 (class 2606 OID 197945)
-- Name: report_seal report_seal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_seal
    ADD CONSTRAINT report_seal_pkey PRIMARY KEY (id);


--
-- TOC entry 3496 (class 2606 OID 206360)
-- Name: report_template report_template_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_template
    ADD CONSTRAINT report_template_pkey PRIMARY KEY (id);


--
-- TOC entry 3491 (class 2606 OID 206323)
-- Name: revaluation_project revaluation_project_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revaluation_project
    ADD CONSTRAINT revaluation_project_pkey PRIMARY KEY (id);


--
-- TOC entry 3488 (class 2606 OID 206311)
-- Name: revaluation_record revaluation_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revaluation_record
    ADD CONSTRAINT revaluation_record_pkey PRIMARY KEY (id);


--
-- TOC entry 3478 (class 2606 OID 197924)
-- Name: survey_photo survey_photo_photo_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey_photo
    ADD CONSTRAINT survey_photo_photo_code_key UNIQUE (photo_code);


--
-- TOC entry 3480 (class 2606 OID 197922)
-- Name: survey_photo survey_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey_photo
    ADD CONSTRAINT survey_photo_pkey PRIMARY KEY (id);


--
-- TOC entry 3469 (class 2606 OID 197841)
-- Name: survey survey_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (id);


--
-- TOC entry 3471 (class 2606 OID 197843)
-- Name: survey survey_survey_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey
    ADD CONSTRAINT survey_survey_code_key UNIQUE (survey_code);


--
-- TOC entry 3449 (class 2606 OID 197742)
-- Name: sys_file_record sys_file_record_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_file_record
    ADD CONSTRAINT sys_file_record_pkey PRIMARY KEY (id);


--
-- TOC entry 3451 (class 2606 OID 197744)
-- Name: sys_file_record sys_file_record_stored_name_key; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_file_record
    ADD CONSTRAINT sys_file_record_stored_name_key UNIQUE (stored_name);


--
-- TOC entry 3447 (class 2606 OID 197730)
-- Name: sys_notification sys_notification_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_notification
    ADD CONSTRAINT sys_notification_pkey PRIMARY KEY (id);


--
-- TOC entry 3486 (class 2606 OID 206292)
-- Name: sys_organization sys_organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sys_organization
    ADD CONSTRAINT sys_organization_pkey PRIMARY KEY (id);


--
-- TOC entry 3435 (class 2606 OID 197679)
-- Name: sys_permission sys_permission_perm_code_key; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_permission
    ADD CONSTRAINT sys_permission_perm_code_key UNIQUE (perm_code);


--
-- TOC entry 3437 (class 2606 OID 197677)
-- Name: sys_permission sys_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_permission
    ADD CONSTRAINT sys_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3443 (class 2606 OID 197706)
-- Name: sys_role_permission sys_role_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role_permission
    ADD CONSTRAINT sys_role_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3429 (class 2606 OID 197662)
-- Name: sys_role sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_pkey PRIMARY KEY (id);


--
-- TOC entry 3431 (class 2606 OID 197666)
-- Name: sys_role sys_role_role_code_key; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_role_code_key UNIQUE (role_code);


--
-- TOC entry 3433 (class 2606 OID 197664)
-- Name: sys_role sys_role_role_name_key; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_role_name_key UNIQUE (role_name);


--
-- TOC entry 3425 (class 2606 OID 197650)
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (id);


--
-- TOC entry 3439 (class 2606 OID 197687)
-- Name: sys_user_role sys_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (id);


--
-- TOC entry 3427 (class 2606 OID 197652)
-- Name: sys_user sys_user_username_key; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_username_key UNIQUE (username);


--
-- TOC entry 3445 (class 2606 OID 197708)
-- Name: sys_role_permission uk_role_perm; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role_permission
    ADD CONSTRAINT uk_role_perm UNIQUE (role_id, permission_id);


--
-- TOC entry 3441 (class 2606 OID 197689)
-- Name: sys_user_role uk_user_role; Type: CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT uk_user_role UNIQUE (user_id, role_id);


--
-- TOC entry 3465 (class 2606 OID 197824)
-- Name: valuation_method valuation_method_method_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_method
    ADD CONSTRAINT valuation_method_method_code_key UNIQUE (method_code);


--
-- TOC entry 3467 (class 2606 OID 197822)
-- Name: valuation_method valuation_method_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_method
    ADD CONSTRAINT valuation_method_pkey PRIMARY KEY (id);


--
-- TOC entry 3484 (class 2606 OID 206279)
-- Name: valuation_price valuation_price_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_price
    ADD CONSTRAINT valuation_price_pkey PRIMARY KEY (id);


--
-- TOC entry 3461 (class 2606 OID 197803)
-- Name: valuation_report valuation_report_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_report
    ADD CONSTRAINT valuation_report_pkey PRIMARY KEY (id);


--
-- TOC entry 3463 (class 2606 OID 197805)
-- Name: valuation_report valuation_report_report_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_report
    ADD CONSTRAINT valuation_report_report_code_key UNIQUE (report_code);


--
-- TOC entry 3423 (class 1259 OID 197639)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: dm
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 3492 (class 1259 OID 206341)
-- Name: idx_reconciliation_record_organization_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reconciliation_record_organization_id ON public.reconciliation_record USING btree (organization_id);


--
-- TOC entry 3489 (class 1259 OID 206324)
-- Name: idx_revaluation_project_revaluation_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_revaluation_project_revaluation_id ON public.revaluation_project USING btree (revaluation_id);


--
-- TOC entry 3476 (class 1259 OID 206282)
-- Name: idx_survey_photo_project_category; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_survey_photo_project_category ON public.survey_photo USING btree (project_id, photo_category);


--
-- TOC entry 3521 (class 2620 OID 197792)
-- Name: collateral trg_collateral_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_collateral_updated_at BEFORE UPDATE ON public.collateral FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3525 (class 2620 OID 206268)
-- Name: ownership_info trg_ownership_info_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_ownership_info_updated_at BEFORE UPDATE ON public.ownership_info FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3520 (class 2620 OID 197773)
-- Name: project trg_project_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_project_updated_at BEFORE UPDATE ON public.project FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3531 (class 2620 OID 206347)
-- Name: reconciliation_record trg_reconciliation_record_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_reconciliation_record_updated_at BEFORE UPDATE ON public.reconciliation_record FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3526 (class 2620 OID 197910)
-- Name: report_review trg_report_review_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_report_review_updated_at BEFORE UPDATE ON public.report_review FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3527 (class 2620 OID 197956)
-- Name: report_seal trg_report_seal_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_report_seal_updated_at BEFORE UPDATE ON public.report_seal FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3532 (class 2620 OID 206361)
-- Name: report_template trg_report_template_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_report_template_updated_at BEFORE UPDATE ON public.report_template FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3530 (class 2620 OID 206325)
-- Name: revaluation_project trg_revaluation_project_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_revaluation_project_updated_at BEFORE UPDATE ON public.revaluation_project FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3529 (class 2620 OID 206312)
-- Name: revaluation_record trg_revaluation_record_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_revaluation_record_updated_at BEFORE UPDATE ON public.revaluation_record FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3524 (class 2620 OID 197849)
-- Name: survey trg_survey_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_survey_updated_at BEFORE UPDATE ON public.survey FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3519 (class 2620 OID 197745)
-- Name: sys_file_record trg_sys_file_record_updated_at; Type: TRIGGER; Schema: public; Owner: dm
--

CREATE TRIGGER trg_sys_file_record_updated_at BEFORE UPDATE ON public.sys_file_record FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3518 (class 2620 OID 197731)
-- Name: sys_notification trg_sys_notification_updated_at; Type: TRIGGER; Schema: public; Owner: dm
--

CREATE TRIGGER trg_sys_notification_updated_at BEFORE UPDATE ON public.sys_notification FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3528 (class 2620 OID 206293)
-- Name: sys_organization trg_sys_organization_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_sys_organization_updated_at BEFORE UPDATE ON public.sys_organization FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3517 (class 2620 OID 197680)
-- Name: sys_permission trg_sys_permission_updated_at; Type: TRIGGER; Schema: public; Owner: dm
--

CREATE TRIGGER trg_sys_permission_updated_at BEFORE UPDATE ON public.sys_permission FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3516 (class 2620 OID 197667)
-- Name: sys_role trg_sys_role_updated_at; Type: TRIGGER; Schema: public; Owner: dm
--

CREATE TRIGGER trg_sys_role_updated_at BEFORE UPDATE ON public.sys_role FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3515 (class 2620 OID 197653)
-- Name: sys_user trg_sys_user_updated_at; Type: TRIGGER; Schema: public; Owner: dm
--

CREATE TRIGGER trg_sys_user_updated_at BEFORE UPDATE ON public.sys_user FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3523 (class 2620 OID 197830)
-- Name: valuation_method trg_valuation_method_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_valuation_method_updated_at BEFORE UPDATE ON public.valuation_method FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3522 (class 2620 OID 197811)
-- Name: valuation_report trg_valuation_report_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_valuation_report_updated_at BEFORE UPDATE ON public.valuation_report FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3502 (class 2606 OID 197787)
-- Name: collateral collateral_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.collateral
    ADD CONSTRAINT collateral_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3514 (class 2606 OID 206342)
-- Name: reconciliation_record fk_reconciliation_record_organization; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reconciliation_record
    ADD CONSTRAINT fk_reconciliation_record_organization FOREIGN KEY (organization_id) REFERENCES public.sys_organization(id) ON DELETE SET NULL;


--
-- TOC entry 3513 (class 2606 OID 206363)
-- Name: sys_organization fk_sys_organization_report_template; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sys_organization
    ADD CONSTRAINT fk_sys_organization_report_template FOREIGN KEY (report_template_id) REFERENCES public.report_template(id) ON DELETE SET NULL;


--
-- TOC entry 3497 (class 2606 OID 206294)
-- Name: sys_user fk_sys_user_organization; Type: FK CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT fk_sys_user_organization FOREIGN KEY (organization_id) REFERENCES public.sys_organization(id) ON DELETE SET NULL;


--
-- TOC entry 3506 (class 2606 OID 197883)
-- Name: ownership_info ownership_info_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ownership_info
    ADD CONSTRAINT ownership_info_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3507 (class 2606 OID 197900)
-- Name: report_review report_review_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_review
    ADD CONSTRAINT report_review_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3508 (class 2606 OID 197905)
-- Name: report_review report_review_report_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_review
    ADD CONSTRAINT report_review_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.valuation_report(id) ON DELETE CASCADE;


--
-- TOC entry 3511 (class 2606 OID 197951)
-- Name: report_seal report_seal_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_seal
    ADD CONSTRAINT report_seal_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3512 (class 2606 OID 197946)
-- Name: report_seal report_seal_report_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_seal
    ADD CONSTRAINT report_seal_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.valuation_report(id) ON DELETE CASCADE;


--
-- TOC entry 3509 (class 2606 OID 197925)
-- Name: survey_photo survey_photo_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey_photo
    ADD CONSTRAINT survey_photo_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3510 (class 2606 OID 197930)
-- Name: survey_photo survey_photo_survey_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey_photo
    ADD CONSTRAINT survey_photo_survey_id_fkey FOREIGN KEY (survey_id) REFERENCES public.survey(id) ON DELETE CASCADE;


--
-- TOC entry 3505 (class 2606 OID 197844)
-- Name: survey survey_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.survey
    ADD CONSTRAINT survey_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


--
-- TOC entry 3500 (class 2606 OID 197714)
-- Name: sys_role_permission sys_role_permission_permission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role_permission
    ADD CONSTRAINT sys_role_permission_permission_id_fkey FOREIGN KEY (permission_id) REFERENCES public.sys_permission(id) ON DELETE CASCADE;


--
-- TOC entry 3501 (class 2606 OID 197709)
-- Name: sys_role_permission sys_role_permission_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_role_permission
    ADD CONSTRAINT sys_role_permission_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.sys_role(id) ON DELETE CASCADE;


--
-- TOC entry 3498 (class 2606 OID 197695)
-- Name: sys_user_role sys_user_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.sys_role(id) ON DELETE CASCADE;


--
-- TOC entry 3499 (class 2606 OID 197690)
-- Name: sys_user_role sys_user_role_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dm
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id) ON DELETE CASCADE;


--
-- TOC entry 3504 (class 2606 OID 197825)
-- Name: valuation_method valuation_method_report_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_method
    ADD CONSTRAINT valuation_method_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.valuation_report(id) ON DELETE CASCADE;


--
-- TOC entry 3503 (class 2606 OID 197806)
-- Name: valuation_report valuation_report_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valuation_report
    ADD CONSTRAINT valuation_report_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE CASCADE;


-- Completed on 2026-06-14 14:22:36 CST

--
-- PostgreSQL database dump complete
--

