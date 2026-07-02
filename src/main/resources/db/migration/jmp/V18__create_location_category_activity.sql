CREATE TABLE zrel_location_category_activity (
    location_category_id VARCHAR(40) NOT NULL,
    activity_id VARCHAR(40) NOT NULL,

    PRIMARY KEY (location_category_id, activity_id),

    CONSTRAINT fk_zrel_loc_cat_act_category
        FOREIGN KEY (location_category_id)
        REFERENCES m_location_category(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_zrel_loc_cat_act_activity
        FOREIGN KEY (activity_id)
        REFERENCES m_activity(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_zrel_loc_cat_act_category
ON zrel_location_category_activity(location_category_id);

CREATE INDEX idx_zrel_loc_cat_act_activity
ON zrel_location_category_activity(activity_id);
