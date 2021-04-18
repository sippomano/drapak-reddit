CREATE TABLE IF NOT EXISTS public.comments
(
    author text COLLATE pg_catalog."default" NOT NULL,
    text text COLLATE pg_catalog."default",
    permalink text COLLATE pg_catalog."default" NOT NULL,
    "post_permalink" text COLLATE pg_catalog."default" NOT NULL,
    "awards_count" numeric NOT NULL,
    score numeric NOT NULL,
    "creation_time" bigint NOT NULL,
    "parent_permalink" text COLLATE pg_catalog."default",
     CONSTRAINT comments_pkey PRIMARY KEY (permalink)
)

    TABLESPACE pg_default;

ALTER TABLE public.comments
    OWNER to CURRENT_USER;