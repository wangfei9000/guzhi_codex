-- V29: Link organizations to report templates

ALTER TABLE sys_organization
    ADD COLUMN IF NOT EXISTS report_template_id BIGINT;

ALTER TABLE sys_organization
    DROP CONSTRAINT IF EXISTS fk_sys_organization_report_template;

ALTER TABLE sys_organization
    ADD CONSTRAINT fk_sys_organization_report_template
    FOREIGN KEY (report_template_id) REFERENCES report_template(id) ON DELETE SET NULL;
