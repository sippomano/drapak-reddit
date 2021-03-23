CREATE TABLE IF NOT EXISTS public.posts
(
    title text COLLATE pg_catalog."default" NOT NULL,
    text text COLLATE pg_catalog."default",
    score numeric NOT NULL,
    permalink text COLLATE pg_catalog."default" NOT NULL,
    flair text COLLATE pg_catalog."default",
    comments_count numeric NOT NULL,
    awards_count numeric NOT NULL,
    author text COLLATE pg_catalog."default" NOT NULL,
    creation_time bigint NOT NULL,
    CONSTRAINT posts_pkey PRIMARY KEY (permalink)
)

    TABLESPACE pg_default;

ALTER TABLE public.posts
    OWNER to CURRENT_USER;