import { Dropzone, IMAGE_MIME_TYPE } from "@mantine/dropzone";
import { SimpleGrid, Text, Group } from "@mantine/core";
import { Upload, X, Image } from 'lucide-react';


export default function ImageDropzone({
                                          existingImages = [],
                                          onExistingImagesChange = () => {},
                                          newFiles = [],
                                          onNewFilesChange = () => {},
                                          label = "Drop images here"
                                      }) {
    const handleDrop = (accepted) => {
        onNewFilesChange([...newFiles, ...accepted]);
    };

    const removeExistingImage = (index) => {
        onExistingImagesChange(existingImages.filter((_, i) => i !== index));
    };

    const removeNewFile = (index) => {
        onNewFilesChange(newFiles.filter((_, i) => i !== index));
    };

    return (
        <div className="space-y-4">
            <Dropzone
                accept={IMAGE_MIME_TYPE}
                onDrop={handleDrop}
                style={{ minHeight: 30 }}
                className="text-center"
                >
                <Group justify="center" gap="xl" mih={20} style={{ pointerEvents: 'none' }}>
                    <Dropzone.Accept>
                        <Upload size={52} color="var(--mantine-color-blue-6)" stroke={1.5} />
                    </Dropzone.Accept>
                    <Dropzone.Reject>
                        <X size={52} color="var(--mantine-color-red-6)" stroke={1.5} />
                    </Dropzone.Reject>
                    <Dropzone.Idle>
                        <Image size={52} color="var(--mantine-color-dimmed)" stroke={1.5} />
                    </Dropzone.Idle>

                    <div>
                        <Text size="xl" inline>
                            Drag images here or click to select files
                        </Text>
                        <Text size="sm" c="dimmed" inline mt={7}>
                            Attach as many files as you like, each file should not exceed 5mb
                        </Text>
                    </div>
                </Group>
            </Dropzone>

            <SimpleGrid cols={{ base: 2, sm: 4 }} mt="sm">
                {existingImages.map((img, i) => (
                    <div key={`existing-${i}`} className="relative">
                        <img src={img} className="rounded-md object-cover w-full h-32" />
                        <button
                            className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-7 h-7 flex items-center justify-center shadow-md hover:bg-red-600"

                            onClick={() => removeExistingImage(i)}
                        >
                            ×
                        </button>
                    </div>
                ))}

                {newFiles.map((file, i) => (
                    <div key={`new-${i}`} className="relative">
                        <img
                            src={URL.createObjectURL(file)}
                            className="rounded-md object-cover w-full h-32"
                        />
                        <button
                            className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-7 h-7 flex items-center justify-center shadow-md hover:bg-red-600"

                            onClick={() => removeNewFile(i)}
                        >
                            ×
                        </button>
                    </div>
                ))}
            </SimpleGrid>
        </div>
    );
}
