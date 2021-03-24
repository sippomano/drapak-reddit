CREATE TABLE IF NOT EXISTS public.comments
(
    author text COLLATE pg_catalog."default" NOT NULL,
    text text COLLATE pg_catalog."default",
    permalink text COLLATE pg_catalog."default" NOT NULL,
    "postPermalink" text COLLATE pg_catalog."default" NOT NULL,
    "awardsCount" numeric NOT NULL,
    score numeric NOT NULL,
    "creationTime" bigint NOT NULL,
    "parentPermalink" text COLLATE pg_catalog."default",
     CONSTRAINT comments_pkey PRIMARY KEY (permalink)
)

    TABLESPACE pg_default;

ALTER TABLE public.comments
    OWNER to CURRENT_USER;