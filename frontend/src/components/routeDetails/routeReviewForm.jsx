import {useState} from "react";
import {Button, Rating, Stack, Group, Textarea, SimpleGrid, Text, Image} from "@mantine/core";
import {Dropzone, IMAGE_MIME_TYPE} from "@mantine/dropzone";

export default function RouteReviewForm({onSubmit}) {
    const [openRateReview, setOpenRateReview] = useState(false);
    const [openReview, setOpenReview] = useState(false);
    const [ratingValue, setRatingValue] = useState(0);
    const [text, setText] = useState("");
    const [files, setFiles] = useState([]);

    const previews = files.map((file, index) => {
        const imageUrl = URL.createObjectURL(file);
        return (
            <div key={index} className="relative">
                <Image src={imageUrl} onLoad={() => URL.revokeObjectURL(imageUrl)} />
                <button
                    onClick={() => setFiles(files.filter((_, i) => i !== index))}
                    className="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center hover:bg-red-600"
                >
                    ×
                </button>
            </div>
        );
    });

    const handleSubmit = async () => {

        await onSubmit({
            ratingValue,
            text,
            images: files
        });

        setRatingValue(0);
        setText("");
        setFiles([]);
        setOpenRateReview(false);
        setOpenReview(false);
    };

    return (
        <div className="text-center">
            <Button
                variant="gradient"
                gradient={{from: "blue", to: "cyan", deg: 199}}
                onClick={() => setOpenRateReview((p) => !p)}
            >
                {openRateReview ? "Hide rate and review" : "Do you want to rate and review?"}
            </Button>

            {openRateReview && (
                <Stack spacing="md" align="center">
                    <div className="text-sm font-medium">
                        Rate your experience exploring this route:
                    </div>
                    <Group>
                        <Rating fractions={2} value={ratingValue} onChange={setRatingValue}/>
                    </Group>

                    <div className="w-full space-y-3">
                        <Button
                            variant="gradient"
                            gradient={{from: "blue", to: "cyan", deg: 199}}
                            onClick={() => setOpenReview((p) => !p)}
                        >
                            {openReview ? "Hide review section" : "Do you want to share your experience?"}
                        </Button>

                        {openReview && (
                            <>
                                <Textarea
                                    variant="filled"
                                    radius="xs"
                                    label="Your thoughts about the route:"
                                    placeholder="You can type your thoughts here."
                                    autosize
                                    minRows={2}
                                    value={text}
                                    onChange={(e) => setText(e.target.value)}
                                />

                                <Dropzone
                                    accept={IMAGE_MIME_TYPE}
                                    onDrop={(acceptedFiles) => {
                                        setFiles(acceptedFiles);
                                    }}
                                >
                                    <Text ta="center">Drop images here</Text>
                                </Dropzone>

                                <SimpleGrid cols={{ base: 1, sm: 4 }} mt={previews.length > 0 ? 'xl' : 0}>
                                    {previews}
                                </SimpleGrid>
                            </>
                        )}
                    </div>

                    <Button
                        variant="gradient"
                        gradient={{from: 'indigo', to: 'violet', deg: 107}}
                        onClick={handleSubmit}
                    >
                        Submit
                    </Button>
                </Stack>
            )}
        </div>
    );
}